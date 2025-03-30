package com.github.cyberxandrew.exception.user;

public class UserMappingException  extends RuntimeException {
    public UserMappingException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserMappingException(String message) {
        super(message);
    }
}
