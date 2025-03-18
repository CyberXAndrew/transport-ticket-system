package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.TicketCreateDTO;
import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.dto.TicketUpdateDTO;
import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.exception.ticket.TicketSaveException;
import com.github.cyberxandrew.mapper.TicketMapper;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.utils.ModelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Sql(scripts = "/test-data/test-data-for-ticket-service-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/test-data/delete-data-for-ticket-service-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class TicketServiceImplIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TicketServiceImpl ticketService;
    private Long testAbsentId;
    private Long availableTicketId;
    private Long unavailableTicketId;
    private Long idOfSavedTicket;

    @BeforeEach
    public void setUp() {
        testAbsentId = -1L;
        idOfSavedTicket = 1L;
        availableTicketId = 1L;
        unavailableTicketId = 4L;
    }

    @Test
    public void testFindTicketById() {
        TicketCreateDTO createDTO = ModelGenerator.createTicketCreateDTO();
        TicketDTO savedTicketDto = ticketService.saveTicket(createDTO);

        TicketDTO ticketDto = ticketService.findTicketById(savedTicketDto.getId());

        assertEquals(ticketDto, savedTicketDto);
        assertThrows(TicketNotFoundException.class, () -> ticketService.findTicketById(testAbsentId));
    }

    @Test
    public void testFindTicketByUserId() {
        TicketCreateDTO createDTO = ModelGenerator.createTicketCreateDTO();
        TicketDTO savedTicketDTO = ticketService.saveTicket(createDTO);

        List<TicketDTO> ticketList = ticketService.findTicketByUserId(savedTicketDTO.getUserId());

        assertFalse(ticketList.isEmpty());
        assertEquals(1, ticketList.size());
        assertEquals(ticketList.get(0), savedTicketDTO);
    }

//    @Test //TODO : cant implement while routes table does not exists
//    public void testFindAllAccessibleTickets() {
//        Ticket ticketToSave = ModelGenerator.createTicketToSave();
//        Ticket savedTicket = ticketService.saveTicket(ticketToSave);
//
//        List<TicketDTO> allAccessibleTickets = ticketService.findAllAccessibleTickets();
//
//        assertFalse(allAccessibleTickets.isEmpty());
//        assertTrue(allAccessibleTickets.contains(savedTicket));
//
//        List<TicketDTO> allAccessibleTickets2 = ticketService.findAllAccessibleTickets(
//                PageRequest.of(0, 2),
//                ticketToSave.getDateTime(),
//                  // paste some test routes to routes table
//                );
//    }

    @Test
    public void testSave() { // UPDATES
        TicketCreateDTO createDTO = ModelGenerator.createTicketCreateDTO();

        TicketDTO savedTicket = ticketService.saveTicket(createDTO);

        assertTrue(savedTicket.getId() != null && savedTicket.getId() > 0);
        assertThrows(TicketSaveException.class, () -> ticketService.saveTicket(null));
    }
    @Test
    public void testUpdate() {
        TicketDTO ticketFromDb = ticketService.findTicketById(idOfSavedTicket);
        TicketUpdateDTO updateDTO = TicketMapper.INSTANCE.ticketDTOToUpdateDTO(ticketFromDb);
        updateDTO.setPrice(new BigDecimal(543.21));
        updateDTO.setDateTime(LocalDateTime.now());

        ticketService.updateTicket(updateDTO, idOfSavedTicket);
        TicketDTO updatedTicketDto = ticketService.findTicketById(idOfSavedTicket);

        assertNotEquals(updatedTicketDto.getDateTime(), updateDTO.getDateTime());
        assertEquals(updatedTicketDto.getUserId(), updateDTO.getUserId() );
        assertEquals(updatedTicketDto.getRouteId(), updateDTO.getRouteId());
        assertNotEquals(updatedTicketDto.getPrice(), updateDTO.getPrice());
        assertEquals(updatedTicketDto.getSeatNumber(), updateDTO.getSeatNumber());
    }

    @Test
    public void testDeleteTicket() {
        ticketService.deleteTicket(idOfSavedTicket);

        assertThrows(TicketNotFoundException.class, () -> ticketService.findTicketById(idOfSavedTicket));
    }
    @Test
    public void testIsTicketAvailable() {
        boolean ticketAvailable = ticketService.isTicketAvailable(availableTicketId);
        boolean ticketAvailable2 = ticketService.isTicketAvailable(unavailableTicketId);

        assertTrue(ticketAvailable);
        assertFalse(ticketAvailable2);
    }
}
