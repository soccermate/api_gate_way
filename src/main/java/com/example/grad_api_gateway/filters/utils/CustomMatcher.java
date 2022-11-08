package com.example.grad_api_gateway.filters.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.AntPathMatcher;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CustomMatcher {
    private final String pattern;

    private final Method method;

    public enum Method{
        POST, GET, PUT, DELETE, PATCH
    }



}

