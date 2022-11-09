package com.example.grad_api_gateway.filters.exceptions;

public class TokenNotFoundException extends RuntimeException
{
    public TokenNotFoundException(String msg)
    {
        super(msg);
    }
}
