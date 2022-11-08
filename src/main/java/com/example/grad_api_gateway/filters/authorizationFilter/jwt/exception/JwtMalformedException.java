package com.example.grad_api_gateway.filters.authorizationFilter.jwt.exception;

public class JwtMalformedException extends RuntimeException{
    public JwtMalformedException(String msg)
    {
        super(msg);
    }
}

