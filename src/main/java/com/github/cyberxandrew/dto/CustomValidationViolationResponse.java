package com.github.cyberxandrew.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomValidationViolationResponse {
    private String timestamp;
    private int status;
    private String error;
    private String massage;
    private List<String> errors;

    private String path;
    private String exception;
}

