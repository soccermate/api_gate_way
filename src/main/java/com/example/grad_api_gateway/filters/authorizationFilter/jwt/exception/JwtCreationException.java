package com.example.grad_api_gateway.filters.authorizationFilter.jwt.exception;

public class JwtCreationException extends RuntimeException {
    public JwtCreationException(String msg)
    {
        super(msg);
    }
}
