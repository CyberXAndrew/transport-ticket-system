package com.github.cyberxandrew.dto.user;

import com.github.cyberxandrew.model.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class UserUpdateDTO {
    private JsonNullable<String> login;
    private JsonNullable<String> password;
    private JsonNullable<String> fullName;
    private JsonNullable<Role> role;
}
