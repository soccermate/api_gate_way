package com.example.grad_api_gateway.filters.authorizationFilter.jwt;

public interface TokenProperties {

    String HEADER_PREFIX = "Bearer ";
    String HEADER_ACCESS_KEY = "ACCESS_TOKEN";
    String HEADER_REFRESH_KEY = "REFRESH_TOKEN";


    Long ACCESS_TOKEN_EXPIRED_TIME = 60000L * 5;
    Long REFRESH_TOKEN_EXPIRED_TIME = 60000L * 60 * 24 * 31;


    String ROLE = "role";

    String CODE = "code";
    String IS_VERIFIED = "is_verified";
    String PURPOSE_CODE = "purpose_code";
    String EMAIL = "email";

}
