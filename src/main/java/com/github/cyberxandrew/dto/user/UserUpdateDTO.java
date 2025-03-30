package com.github.cyberxandrew.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateDTO {
    private String login;
    private String password;
    private String fullName;
}
