package com.github.cyberxandrew.exception.ticket;

public class TicketDeleteException extends  RuntimeException {
    public TicketDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
    public TicketDeleteException(String message) {
        super(message);
    }
}
