package com.github.cyberxandrew.exception.route;

public class RouteDeletionException extends RuntimeException {
    public RouteDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
    public RouteDeletionException(String message) {
        super(message);
    }
}
