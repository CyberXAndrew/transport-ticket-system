package com.github.cyberxandrew.exception.user;

public class UserHasTicketsException extends RuntimeException {
    public UserHasTicketsException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserHasTicketsException(String message) {
        super(message);
    }
}
