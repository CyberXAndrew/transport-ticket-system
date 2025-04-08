package com.github.cyberxandrew.dto.user;

import com.github.cyberxandrew.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank
    private String login;
    @NotBlank
    private String password;
    @NotBlank
    private String fullName;
    @NotNull
    private Role role;
}
