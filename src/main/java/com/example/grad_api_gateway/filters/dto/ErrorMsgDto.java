package com.example.grad_api_gateway.filters.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ErrorMsgDto {

    private final Date timestamp;
    private final String msg;
    private final String details;
}
