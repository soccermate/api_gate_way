package com.example.grad_api_gateway.filters.authorizationFilter.jwt.exception;

public class JwtNotFoundException extends RuntimeException{
    public JwtNotFoundException(String msg)
    {
        super(msg);
    }
}
