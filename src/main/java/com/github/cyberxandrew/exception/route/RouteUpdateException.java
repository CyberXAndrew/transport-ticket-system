package com.github.cyberxandrew.exception.route;

public class RouteUpdateException extends RuntimeException {
    public RouteUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
    public RouteUpdateException(String message) {
        super(message);
    }
}
