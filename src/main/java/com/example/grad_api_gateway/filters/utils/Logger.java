package com.example.grad_api_gateway.filters.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class Logger {
    private StringBuilder sb = new StringBuilder();

    private static final Set<String> LOGGABLE_CONTENT_TYPES = new HashSet<>(Arrays.asList(
            MediaType.APPLICATION_JSON_VALUE.toLowerCase(),
            MediaType.TEXT_PLAIN_VALUE.toLowerCase(),
            MediaType.TEXT_XML_VALUE.toLowerCase()
    ));

    public Logger(ServerHttpResponse response) {
        sb.append("\n");
        sb.append("---- Response -----").append("\n");
        sb.append("Headers      :").append(response.getHeaders().toSingleValueMap()).append("\n");
        sb.append("Status code  :").append(response.getStatusCode()).append("\n");
    }

    public Logger(ServerHttpRequest request) {
        sb.append("\n");
        sb.append("---- Request -----").append("\n");
        sb.append("Headers      :").append(request.getHeaders().toSingleValueMap()).append("\n");
        sb.append("Path         :").append(request.getURI().getPath()).append("\n");
        sb.append("Method       :").append(request.getMethod()).append("\n");
        sb.append("Client       :").append(request.getRemoteAddress()).append("\n");
    }


    public void appendBody(ByteBuffer byteBuffer) {
        sb.append("Body         :").append(StandardCharsets.UTF_8.decode(byteBuffer)).append("\n");
    }

    public void appendBody(String str) {
        sb.append("Body         :").append(str).append("\n");
    }

    public void log() {
        sb.append("-------------------").append("\n");
        log.info(sb.toString());
    }

    public static boolean isLoggable(ServerHttpRequest request)
    {
        if(request.getHeaders().getContentType() == null) {
            log.info("content tpe is null!");
            return false;
        }

        log.info(request.getHeaders().getContentType().toString());
        String contentType = request.getHeaders().getContentType().toString();
        for(String allowedContentType: LOGGABLE_CONTENT_TYPES)
        {
            if(contentType.contains(allowedContentType))
            {
                return true;
            }
        }
        return false;
    }


}