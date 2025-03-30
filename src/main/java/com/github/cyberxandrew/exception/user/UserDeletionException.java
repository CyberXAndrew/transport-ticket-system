package com.github.cyberxandrew.exception.user;

public class UserDeletionException extends RuntimeException {
    public UserDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserDeletionException(String message) {
        super(message);
    }
}
