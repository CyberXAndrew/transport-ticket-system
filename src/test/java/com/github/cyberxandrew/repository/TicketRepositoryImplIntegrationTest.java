package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.exception.ticket.TicketDeletionException;
import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.utils.ModelGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        Ticket ticketToSave = ModelGenerator.createTicketToSave();

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
        Ticket ticketToSave = ModelGenerator.createTicketToSave();
        Ticket savedTicket = ticketRepository.save(ticketToSave);

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
//        jdbcTemplate.execute("""
//                INSERT INTO users (id, login, password, name, surname, middle_name)
//                VALUES (1, 'testLogin', 'testPassword', 'testName', 'testSurname', 'testMiddleName');
//                """);
//
//        jdbcTemplate.execute("""
//                INSERT INTO tickets (id, date_time, user_id, route_id, price, seat_number)
//                VALUES (2, '2999-01-01 00:00:00', 1, 1, 15.5, '1A');
//                """);
//        Ticket ticketToSave = prepareTicketToSave();
//        ticketRepository.save(ticketToSave);
//        ticketRepository.save(prepareTicketToSave());
//
//        String sql = "DELETE FROM tickets WHERE id = ?";
//
//        assertThrows(TicketDeletionException.class, () -> ticketRepository.deleteById(testTicketId));
    }
}
