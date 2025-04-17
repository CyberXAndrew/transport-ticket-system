package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.utils.TicketFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class TicketRepositoryImplIntegrationTest {
    @Autowired private TicketRepositoryImpl ticketRepository;

    private final Long NOT_EXISTING_ID = 999L;

    @Test
    @Transactional
    public void testSave() {
        Ticket ticketToSave = TicketFactory.createTicketToSave();

        Ticket savedTicket = ticketRepository.save(ticketToSave);

        assertNotNull(savedTicket.getId());
        assertTrue(savedTicket.getId() > 0);
        assertEquals(savedTicket.getUserId(), ticketToSave.getUserId());
        assertEquals(savedTicket.getRouteId(), ticketToSave.getRouteId());
        assertEquals(savedTicket.getPrice(), ticketToSave.getPrice());
        assertEquals(savedTicket.getSeatNumber(), ticketToSave.getSeatNumber());
    }

    @Test
    @Transactional
    public void testDeleteByIdSuccessful() {
        Ticket ticketToSave = TicketFactory.createTicketToSave();
        Ticket savedTicket = ticketRepository.save(ticketToSave);

        assertTrue(savedTicket.getId() != null && savedTicket.getId() > 0);

        ticketRepository.deleteById(savedTicket.getId());

        assertThrows(TicketNotFoundException.class, () -> ticketRepository.findById(savedTicket.getId()));

    }

    @Test
    @Transactional
    public void testDeleteByIdFailed() {
        assertThrows(TicketNotFoundException.class,
                () -> ticketRepository.deleteById(NOT_EXISTING_ID));
    }

    @Test
    @Transactional
    public void testDeleteByIdDataBaseError() {// TODO
    }
}
