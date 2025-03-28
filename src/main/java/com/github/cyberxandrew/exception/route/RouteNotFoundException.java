package com.github.cyberxandrew.exception.route;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public RouteNotFoundException(String message) {
        super(message);
    }
}
