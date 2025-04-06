package com.github.cyberxandrew.exception;

public class JwtRequestFilterException extends RuntimeException {
    public JwtRequestFilterException(String message, Throwable cause) {
        super(message, cause);
    }
    public JwtRequestFilterException(String message) {
        super(message);
    }
}
