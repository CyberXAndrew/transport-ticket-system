package com.github.cyberxandrew.exception.route;

public class RouteMappingException  extends RuntimeException {
    public RouteMappingException(String message, Throwable cause) {
        super(message, cause);
    }
    public RouteMappingException(String message) {
        super(message);
    }
}
