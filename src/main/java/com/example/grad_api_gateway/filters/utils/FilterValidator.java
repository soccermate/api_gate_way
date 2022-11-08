package com.example.grad_api_gateway.filters.utils;

import com.example.grad_api_gateway.filters.authorizationFilter.jwt.model.Role;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FilterValidator {

    private static final Set<CustomMatcher> ALL_PERMITTED_MATCHERS = new HashSet<CustomMatcher>(
            Arrays.asList(
                    new CustomMatcher("/user/signup/emailsend", CustomMatcher.Method.POST),
                    new CustomMatcher("/user/signup/emailcodecheck", CustomMatcher.Method.POST),
                    new CustomMatcher("/user", CustomMatcher.Method.POST),
                    new CustomMatcher("/user/password/emailsend", CustomMatcher.Method.POST),
                    new CustomMatcher("/user/password/emailcodecheck", CustomMatcher.Method.POST),
                    new CustomMatcher("/user/password", CustomMatcher.Method.PUT),
                    new CustomMatcher("/user/token/refresh", CustomMatcher.Method.POST),
                    new CustomMatcher("/user/login", CustomMatcher.Method.POST),
                    new CustomMatcher("/oauth2/authorization/google", CustomMatcher.Method.POST),
                    new CustomMatcher("/oauth2/authorization/google", CustomMatcher.Method.GET),
                    new CustomMatcher("/actuator/**", CustomMatcher.Method.GET)
            )
    );

    private static final Set<CustomMatcher> REGISTRATION_NOT_COMPLETE_MATCHER = new HashSet<>(
            Arrays.asList(
                    new CustomMatcher("/user", CustomMatcher.Method.PUT )
            )
    );

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    //check if a user with a role can access to the endpoint
    public static boolean isPermitted(ServerHttpRequest request, Role role)
    {
        String path = request.getURI().getPath();
        CustomMatcher.Method method = Enum.valueOf(CustomMatcher.Method.class,request.getMethod().toString());

        if(role.equals(Role.USER) || role.equals(Role.ADMIN))
        {
            return true;
        }
        else if(role.equals(Role.REGISTRATION_NOT_COMPLETE_USER))
        {
            //Registration Not complete에 있는 Matcher랑 일치하면 ok!
            for(CustomMatcher matcher: REGISTRATION_NOT_COMPLETE_MATCHER)
            {
                if(doesMatch(matcher, path, method))
                    return true;
            }

            return false;
        }
        else{
            return false;
        }

    }

    public static boolean shouldBeValidated(ServerHttpRequest request)
    {
        String path = request.getURI().getPath();
        CustomMatcher.Method method = Enum.valueOf(CustomMatcher.Method.class,request.getMethod().toString());

        for(CustomMatcher customMatcher: ALL_PERMITTED_MATCHERS)
        {
            if(doesMatch(customMatcher, path, method))
                return false;
        }

        return true;

    }

    private static boolean doesMatch(CustomMatcher customMatcher, String pathToCompare, CustomMatcher.Method methodToCompare)
    {
        return antPathMatcher.match(customMatcher.getPattern(), pathToCompare) && methodToCompare.equals(customMatcher.getMethod());
    }

}
