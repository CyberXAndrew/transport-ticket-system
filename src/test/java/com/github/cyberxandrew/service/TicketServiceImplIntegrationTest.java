package com.github.cyberxandrew.service;

import com.github.cyberxandrew.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TicketServiceImplIntegrationTest {
    @Autowired private TicketServiceImpl ticketService;
    private Long testTicketId;
    private Ticket testTicket;

    @BeforeEach
    public void setUp() {
        testTicketId = 1L;

        testTicket.setId(null);
    }

    @Test
    @Transactional
    public void testFindTicketById() {
        Ticket savedTicket = ticketService.saveTicket(testTicket);

        Ticket ticketById = ticketService.findTicketById(savedTicket.getId());

        assertEquals (ticketById, savedTicket);
    }

}
