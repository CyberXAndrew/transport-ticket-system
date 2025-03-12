package exception.ticket;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public TicketNotFoundException(String message) {
        super(message);
    }
}
