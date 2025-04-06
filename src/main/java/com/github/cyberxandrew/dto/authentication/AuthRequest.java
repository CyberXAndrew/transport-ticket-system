package com.github.cyberxandrew.dto.authentication;

import lombok.Data;

@Data
public class AuthRequest {
    private String login;
    private String password;
}
