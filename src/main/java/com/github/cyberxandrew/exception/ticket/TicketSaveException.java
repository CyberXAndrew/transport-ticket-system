package com.github.cyberxandrew.exception.ticket;

public class TicketSaveException extends RuntimeException {
    public TicketSaveException(String message, Throwable cause) {
        super(message, cause);
    }
    public TicketSaveException(String message) {
        super(message);
    }
}
