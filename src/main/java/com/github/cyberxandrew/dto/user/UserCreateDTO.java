package com.github.cyberxandrew.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank
    private String login;
    @NotBlank
    private String password;
    @NotBlank
    private String fullName;
}
