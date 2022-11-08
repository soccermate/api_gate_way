package com.example.grad_api_gateway.filters;


import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.grad_api_gateway.filters.authorizationFilter.jwt.exception.JwtMalformedException;
import com.example.grad_api_gateway.filters.authorizationFilter.jwt.exception.JwtNotFoundException;
import com.example.grad_api_gateway.filters.dto.ErrorMsgDto;
import com.example.grad_api_gateway.filters.exceptions.UserNotAuthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error(ex.getMessage());

        ServerHttpResponse response = exchange.getResponse();

        ErrorMsgDto errorMsgDto = new ErrorMsgDto(new Date(), "error occurred in gateway",ex.getMessage() );

        HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;

        if(ex instanceof JWTVerificationException)
        {
            statusCode = HttpStatus.UNAUTHORIZED;
        }
        else if(ex instanceof JwtMalformedException)
        {
            statusCode = HttpStatus.BAD_REQUEST;
        }
        else if(ex instanceof JwtNotFoundException)
        {
            statusCode = HttpStatus.BAD_REQUEST;
        }
        else if(ex instanceof UserNotAuthorizedException)
        {
            statusCode = HttpStatus.UNAUTHORIZED;
        }

        //set status code and content type
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(statusCode);

        //process of writing body for the response
        String bodyStr = null;

        try {
            bodyStr = objectMapper.writeValueAsString(errorMsgDto);
        }
        catch (Exception e)
        {
            log.error("error occurred while writing error message in gateway");
            log.error(e.toString());
        }
        finally {
            if(bodyStr == null)
            {
                bodyStr = "error occurred while writing error message in gateway";
            }
        }

        //change the json String  to bytes
        byte[] bytes = bodyStr.getBytes(StandardCharsets.UTF_8);

        //change byte array to Databuffer to write to response body
        return exchange.getResponse()
                .writeWith(Flux.just(response.bufferFactory().wrap(bytes)));

    }
}
