package com.github.cyberxandrew.exception.route;

public class RouteHasRoutesException extends RuntimeException {
    public RouteHasRoutesException(String message, Throwable cause) {
        super(message, cause);
    }
    public RouteHasRoutesException(String message) {
        super(message);
    }
}
