package exception.ticket;

public class TicketAvailabilityException extends RuntimeException {
    public TicketAvailabilityException(String message, Throwable cause) {
        super(message, cause);
    }
    public TicketAvailabilityException(String message) {
        super(message);
    }
}
