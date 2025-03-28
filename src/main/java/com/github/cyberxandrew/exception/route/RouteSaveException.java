package com.github.cyberxandrew.exception.route;

public class RouteSaveException extends RuntimeException {
    public RouteSaveException(String message, Throwable cause) {
        super(message, cause);
    }
    public RouteSaveException(String message) {
        super(message);
    }
}
