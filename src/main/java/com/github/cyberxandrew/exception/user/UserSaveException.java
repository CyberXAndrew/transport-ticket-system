package com.github.cyberxandrew.exception.user;

public class UserSaveException extends RuntimeException {
    public UserSaveException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserSaveException(String message) {
        super(message);
    }
}
