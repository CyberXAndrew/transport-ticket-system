package com.github.cyberxandrew.service;

import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.utils.ModelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TicketServiceImplIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TicketServiceImpl ticketService;
    private Long testAbsentId;
    private Long testTicketId;
    //    private Ticket testTicket;
    private Long testUserId;

    @BeforeEach
    public void setUp() {
        testTicketId = 1L;
        testAbsentId = -1L;
        testUserId = 2L;
//        testTicket.setId(null);

        jdbcTemplate.execute("DROP TABLE IF EXISTS tickets;");
        jdbcTemplate.execute("CREATE TABLE tickets (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "date_time TIMESTAMP NOT NULL, " +
                "user_id BIGINT, " +
                "route_id BIGINT NOT NULL, " +
                "price DECIMAL (10,2) NOT NULL, " +
                "seat_number VARCHAR(255) NOT NULL" +
                ");");
    }

    @Test
    public void testFindTicketById() {
        Ticket ticketToSave = ModelGenerator.createTicketToSave();
        System.out.println(ticketToSave.getDateTime().toString() + " OPOPOPOOOP ");
        Ticket savedTicket = ticketService.saveTicket(ticketToSave);

        System.out.println(savedTicket.toString() + " OPOPOPOOOP 22222");
        System.out.println(savedTicket.getDateTime().toString() + " OPOPOPOOOP 22222");
        Ticket ticketById = ticketService.findTicketById(savedTicket.getId());//TYT Возвращает БДшный формат на 3 цифры меньше
        System.out.println(ticketById.getDateTime().toString() + " OPOPOPOOOP 333333");

        assertEquals(ticketById, savedTicket);
    }

    @Test
    public void testFindTicketByIdThrowsException() {
        assertThrows(TicketNotFoundException.class, () -> ticketService.findTicketById(testAbsentId));
    }

    @Test
    public void testFindTicketByUserId() {
        Ticket ticketToSave = ModelGenerator.createTicketToSave();
        Ticket savedTicket = ticketService.saveTicket(ticketToSave);

        List<Ticket> ticketList = ticketService.findTicketByUserId(savedTicket.getUserId());

        assertFalse(ticketList.isEmpty());
        assertEquals(ticketList.size(), 1);
        assertEquals(ticketList.get(1), savedTicket);
    }
//    @Test
//    public void testSave() { // UPDATES
//}
//    @Test
//    public void test
//    @Test
//    public void test
//    @Test
//    public void test

}
