package com.github.cyberxandrew.exception.route;

public class RouteHasTicketsException extends RuntimeException {
    public RouteHasTicketsException(String message, Throwable cause) {
        super(message, cause);
    }
    public RouteHasTicketsException(String message) {
        super(message);
    }
}
