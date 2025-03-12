package repository;

import model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("test")
public class TicketRepositoryImplIntegrationTest {
    @Autowired
    private TicketRepositoryImpl ticketRepository;

    @Test
    @Transactional
    public void testSave() {
        Ticket ticketToSave = prepareTicketToSave();

        Ticket savedTicket = ticketRepository.save(ticketToSave);

        assertNotNull(savedTicket.getId());
        assertTrue(savedTicket.getId() > 0);
        assertEquals(savedTicket.getUserId(), ticketToSave.getUserId());
        assertEquals(savedTicket.getRouteId(), ticketToSave.getRouteId());
        assertEquals(savedTicket.getPrice(), ticketToSave.getPrice());
        assertEquals(savedTicket.getSeatNumber(), ticketToSave.getSeatNumber());

    }

    private Ticket prepareTicketToSave() {
        Ticket ticket = new Ticket();
        ticket.setId(null);
        ticket.setDateTime(LocalDateTime.now());
        ticket.setUserId(2L);
        ticket.setRouteId(3L);
        ticket.setPrice(new BigDecimal("15.5"));
        ticket.setSeatNumber("1A");
        return ticket;
    }
}
