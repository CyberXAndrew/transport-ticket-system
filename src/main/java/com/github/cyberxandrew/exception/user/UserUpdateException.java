package com.github.cyberxandrew.exception.user;

public class UserUpdateException extends RuntimeException {
    public UserUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserUpdateException(String message) {
        super(message);
    }
}
