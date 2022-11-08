package com.example.grad_api_gateway.filters.authorizationFilter.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.grad_api_gateway.filters.authorizationFilter.jwt.dto.VerifyTokenResultDto;
import com.example.grad_api_gateway.filters.authorizationFilter.jwt.exception.JwtMalformedException;
import com.example.grad_api_gateway.filters.authorizationFilter.jwt.exception.JwtNotFoundException;
import com.example.grad_api_gateway.filters.authorizationFilter.jwt.model.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static javax.crypto.Cipher.SECRET_KEY;

@Component
@Slf4j
public class TokenService {

    @Value("${jwt.token.secretKey}")
    private String SECRET_KEY;

    public VerifyTokenResultDto verifyAccessToken(String jwtToken)
    {
        String extractedToken = extract(jwtToken);
        VerifyTokenResultDto verifyTokenResultDto = verifyToken(extractedToken);

        if(!verifyTokenResultDto.isValid())
        {
            throw new JwtMalformedException("the token: " +jwtToken + " is malformed!");
        }

        return verifyTokenResultDto;
    }

    //verify token before accessing protected resources
    private VerifyTokenResultDto verifyToken(String jwtToken)
    {
        DecodedJWT decodedJWT = decodeJwtToken(jwtToken);
        long user_id;
        Role role;

        try {
            user_id = Long.valueOf(decodedJWT.getSubject());
            role = Enum.valueOf(Role.class, decodedJWT.getClaim(TokenProperties.ROLE).asString());
        }
        catch (Exception e)
        {
            log.error("error occurred while verifying token");
            log.error(e.toString());
            return VerifyTokenResultDto.getInvalidInstance();
        }
        return new VerifyTokenResultDto(user_id, role);

    }

    private DecodedJWT decodeJwtToken(String jwtToken)
    {
        return JWT.require(Algorithm.HMAC512(SECRET_KEY)).build().verify(jwtToken);
    }

    private String extract(String jwtTokenHeader) {
        if(!StringUtils.hasText(jwtTokenHeader)){
            throw new JwtNotFoundException("Authorization header cannot be blank!");
        }

        if(jwtTokenHeader.length() < TokenProperties.HEADER_PREFIX.length()){
            throw new JwtNotFoundException("Invalid authorization header size");
        }

        if(!jwtTokenHeader.startsWith(TokenProperties.HEADER_PREFIX)){
            throw new JwtNotFoundException("Invalid token format");
        }

        return jwtTokenHeader.substring(TokenProperties.HEADER_PREFIX.length());
    }
}
