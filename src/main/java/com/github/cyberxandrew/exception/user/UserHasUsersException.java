package com.github.cyberxandrew.exception.user;

public class UserHasUsersException extends RuntimeException {
    public UserHasUsersException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserHasUsersException(String message) {
        super(message);
    }
}
