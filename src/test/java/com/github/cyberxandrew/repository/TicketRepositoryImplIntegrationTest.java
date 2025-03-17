package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.dto.TicketCreateDTO;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.utils.ModelGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class TicketRepositoryImplIntegrationTest {
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private TicketRepositoryImpl ticketRepository;

    private final Long NOT_EXISTING_ID = 9999999L;
    private Long testTicketId;
    private Long testUserId;
    private Long testRouteId;
    private BigDecimal testPrice;
    private String testSeatNumber;
    private String testDeparturePoint;
    private String testDestinationPoint;
    private String testCarrierName;
    private Ticket testTicket;
    @BeforeEach
    void beforeEach() {
        testTicketId = 1L;
        testUserId = 2L;
        testRouteId = 3L;
        testPrice = new BigDecimal("123.45");
        testSeatNumber = "1A";
        testDeparturePoint = "Saints-Petersburg";
        testDestinationPoint = "Moscow";
        testCarrierName = "Java Airlines";
    }

    @Test
    @Transactional
    public void testSave() {
        TicketCreateDTO createDTO = ModelGenerator.createTicketToSave();

        Ticket savedTicket = ticketRepository.save(createDTO);

        assertNotNull(savedTicket.getId());
        assertTrue(savedTicket.getId() > 0);
        assertEquals(savedTicket.getUserId(), createDTO.getUserId());
        assertEquals(savedTicket.getRouteId(), createDTO.getRouteId());
        assertEquals(savedTicket.getPrice(), createDTO.getPrice());
        assertEquals(savedTicket.getSeatNumber(), createDTO.getSeatNumber());
    }

    @Test
    @Transactional
    public void testDeleteByIdSuccessful() {
        TicketCreateDTO createDTO = ModelGenerator.createTicketToSave();
        Ticket savedTicket = ticketRepository.save(createDTO);

        assertTrue(savedTicket.getId() != null && savedTicket.getId() > 0);

        ticketRepository.deleteById(savedTicket.getId());

        Optional<Ticket> ticketOptional = ticketRepository.findById(savedTicket.getId());
        assertTrue(ticketOptional.isEmpty());
    }

    @Test
    @Transactional
    public void testDeleteByIdFailed() {
        Optional<Ticket> ticketOptional = ticketRepository.findById(NOT_EXISTING_ID);
        assertTrue(ticketOptional.isEmpty());
        assertThrows(TicketNotFoundException.class,
                () -> ticketRepository.deleteById(NOT_EXISTING_ID));
    }

    @Test
    @Transactional
    public void testDeleteByIdDataBaseError() {// TODO
        // ? TODO
    }
}
