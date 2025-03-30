package com.github.cyberxandrew.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String login;
    private String password;
    private String fullName;
}
