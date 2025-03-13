package com.github.cyberxandrew.exception.ticket;

public class TicketMappingException  extends RuntimeException {
    public TicketMappingException(String message, Throwable cause) {
        super(message, cause);
    }
    public TicketMappingException(String message) {
        super(message);
    }
}
