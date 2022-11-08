package com.example.grad_api_gateway.config;


import com.example.grad_api_gateway.filters.LoggingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class ApiGatewayRouteConfig {

    @Value("${msa.path.authentication}")
    private String authenticationPath;

    @Value("${msa.path.user}")
    private String userPath;

    @Value("${msa.path.soccer-group}")
    private String soccerGroupPath;

    @Value("${msa.path.announcements}")
    private String announcementsPath;

    @Value("${msa.path.votes}")
    private String votesPath;

    @Value("${msa.path.meetings}")
    private String meetingsPath;

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder)
    {


        return builder.routes()
                .route(p -> p
                        .path("/oauth2/authorization/google" ,"/user/token/**","/user/password/**",
                                "/user/login", "/user/signup/**")
                        .filters(f -> f.rewritePath("/(?<segment>.*)",
                                "/${segment}"))
                        .uri(authenticationPath))
                .route(p -> p
                        .method(HttpMethod.POST)
                        .and()
                        .path("/user")
                        .and()
                        .not(n -> n.path("/user/{id}/role"))
                        .filters(f -> f.rewritePath("/user/(?<segment>.*)",
                                "/user/${segment}"))

                        .uri(authenticationPath))
                .route(p ->
                        p.path("/user/**")
                                .filters(f -> f.rewritePath("/user/(?<segment>.*)",
                                        "/user/${segment}"))
                                .uri(userPath)
                )
                .route(p ->
                        p.path("/soccer-group/{groupId}/announcements/**")

                                .uri(announcementsPath)
                )
                .route(p ->
                        p.path("/soccer-group/{groupId}/votes/**")

                                .uri(votesPath)
                )
                .route(p ->
                        p.path("/soccer-group/{groupId}/meetings/**")

                                .uri(meetingsPath)
                )
                .route(p ->
                    p.path("/soccer-group/**")
                    .filters(f -> f.rewritePath("/soccer-group/(?<segment>.*)",
                            "/soccer-group/${segment}"))
                    .uri(soccerGroupPath)
                )
                .build();

    }

    @Bean
    public GlobalFilter getLoggingFilter()
    {
        return new LoggingFilter();
    }

}
