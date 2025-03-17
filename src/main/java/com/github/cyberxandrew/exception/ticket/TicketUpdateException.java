package com.github.cyberxandrew.exception.ticket;

public class TicketUpdateException extends RuntimeException {
    public TicketUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
    public TicketUpdateException(String message) {
        super(message);
    }
}
