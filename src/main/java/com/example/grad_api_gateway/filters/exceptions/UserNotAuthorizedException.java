package com.example.grad_api_gateway.filters.exceptions;

public class UserNotAuthorizedException extends RuntimeException{
    public UserNotAuthorizedException(String msg)
    {
        super(msg);
    }

}
