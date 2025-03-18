package com.github.cyberxandrew.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class User {
    private Long id;
    @NotBlank
    private String login;
    @NotBlank
    private String password;
    @NotBlank
    private String fullName;
}
