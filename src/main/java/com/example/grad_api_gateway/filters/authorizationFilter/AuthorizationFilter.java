package com.example.grad_api_gateway.filters.authorizationFilter;

import com.example.grad_api_gateway.filters.authorizationFilter.jwt.TokenService;
import com.example.grad_api_gateway.filters.authorizationFilter.jwt.dto.VerifyTokenResultDto;
import com.example.grad_api_gateway.filters.authorizationFilter.jwt.exception.JwtNotFoundException;
import com.example.grad_api_gateway.filters.authorizationFilter.jwt.model.Role;
import com.example.grad_api_gateway.filters.exceptions.TokenNotFoundException;
import com.example.grad_api_gateway.filters.exceptions.UserNotAuthorizedException;
import com.example.grad_api_gateway.filters.utils.FilterValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZATION = "authorization";
    private static final String AUTH_CREDENTIALS = "auth_credentials";

    private final TokenService tokenService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){


        if(!FilterValidator.shouldBeValidated(exchange.getRequest()))
        {
            log.info("should not be validated!");
            return chain.filter(exchange);
        }

        List<String> temp = exchange.getRequest().getHeaders().get(AUTHORIZATION);
        if(temp == null )
        {
            throw new TokenNotFoundException("token is not found");
        }

        String jwtToken = temp.get(0);

        if(jwtToken == null)
        {
            throw new JwtNotFoundException("jwt token not found!");
        }

        VerifyTokenResultDto verifyTokenResultDto = tokenService.verifyAccessToken(jwtToken);

        Role userRole = verifyTokenResultDto.getRole();

        if(!FilterValidator.isPermitted(exchange.getRequest(), userRole))
        {
            throw new UserNotAuthorizedException("user with role: " + userRole.toString() + " is not authorized to access the path");
        }


        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(
                        headers -> {
                            try {
                                headers.add(AUTH_CREDENTIALS, objectMapper.writeValueAsString(verifyTokenResultDto));
                            }
                            catch (Exception ex)
                            {
                                log.error(ex.toString());
                            }
                            headers.remove(AUTHORIZATION);
                        })
                .build();


        log.info("should be validated!");
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
