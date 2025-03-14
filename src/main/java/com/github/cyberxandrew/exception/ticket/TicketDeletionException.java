package com.github.cyberxandrew.exception.ticket;

public class TicketDeletionException extends  RuntimeException {
    public TicketDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
    public TicketDeletionException(String message) {
        super(message);
    }
}
