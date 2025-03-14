package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.exception.ticket.TicketDeleteException;
import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

        jdbcTemplate.execute("DROP TABLE IF EXISTS tickets;");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users;");
        jdbcTemplate.execute("CREATE TABLE users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "login VARCHAR (255) NOT NULL UNIQUE," +
                "password VARCHAR (255) NOT NULL," +
                "name VARCHAR (255) NOT NULL," +
                "surname VARCHAR (255) NOT NULL," +
                "middle_name VARCHAR (255));");
        jdbcTemplate.execute("CREATE TABLE tickets (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "date_time TIMESTAMP NOT NULL, " +
                "user_id BIGINT, " +
                "route_id BIGINT NOT NULL, " +
                "price DECIMAL (10,2) NOT NULL, " +
                "seat_number VARCHAR(255) NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ");");
    }

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

    @Test
    @Transactional
    public void testDeleteByIdSuccessful() {
        Ticket ticketToSave = prepareTicketToSave();
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
//        Long testTicketId = 2L;

//        jdbcTemplate.execute("""
//                INSERT INTO users (id, login, password, name, surname, middle_name)
//                VALUES (1, 'testLogin', 'testPassword', 'testName', 'testSurname', 'testMiddleName');
//                """);

//        jdbcTemplate.execute("""
//                INSERT INTO tickets (id, date_time, user_id, route_id, price, seat_number)
//                VALUES (2, '2999-01-01 00:00:00', 1, 1, 15.5, '1A');
//                """);
//
//        assertThrows(TicketDeleteException.class, () -> ticketRepository.deleteById(testTicketId));
    }

    private Ticket prepareTicketToSave() {
        Ticket ticket = new Ticket();
        ticket.setId(null);
        ticket.setDateTime(LocalDateTime.now());
        ticket.setUserId(testUserId);
        ticket.setRouteId(testRouteId);
        ticket.setPrice(testPrice);
        ticket.setSeatNumber(testSeatNumber);
        return ticket;
    }
}
