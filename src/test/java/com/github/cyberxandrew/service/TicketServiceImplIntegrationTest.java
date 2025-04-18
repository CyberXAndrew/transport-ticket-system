package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.ticket.TicketCreateDTO;
import com.github.cyberxandrew.dto.ticket.TicketDTO;
import com.github.cyberxandrew.dto.ticket.TicketUpdateDTO;
import com.github.cyberxandrew.dto.ticket.TicketWithRouteDataDTO;
import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.exception.ticket.TicketSaveException;
import com.github.cyberxandrew.utils.TicketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class TicketServiceImplIntegrationTest {
    @Autowired private TicketServiceImpl ticketService;
    private Long testAbsentId;
    private Long availableTicketId;
    private Long unavailableTicketId;
    private Long idOfSavedTicket;
    private Long userId;

    @BeforeEach
    public void setUp() {
        testAbsentId = -1L;
        idOfSavedTicket = 1L;
        availableTicketId = 1L;
        unavailableTicketId = 4L;
        userId = 2L;
    }

    @Test
    public void testFindTicketById() {
        TicketCreateDTO createDTO = TicketFactory.createTicketCreateDTO();
        TicketDTO savedTicketDto = ticketService.saveTicket(createDTO);

        TicketDTO ticketDto = ticketService.findTicketById(savedTicketDto.getId());

        assertEquals(ticketDto, savedTicketDto);
        assertThrows(TicketNotFoundException.class, () -> ticketService.findTicketById(testAbsentId));
    }

    @Test
    public void testFindAllPurchasedTickets() {
        TicketCreateDTO createDTO = TicketFactory.createTicketCreateDTO();
        TicketDTO savedTicketDTO = ticketService.saveTicket(createDTO);

        List<TicketDTO> ticketList = ticketService.findAllPurchasedTickets(savedTicketDTO.getUserId());

        assertFalse(ticketList.isEmpty());
        assertEquals(1, ticketList.size());
        assertEquals(ticketList.getFirst(), savedTicketDTO);
    }

    @Test
    public void testFindAllAccessibleTicketsWithAllParams() {
        int pageSize = 1;
        int pageNumber = 0;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<TicketWithRouteDataDTO> allAccessibleTickets = ticketService.findAllAccessibleTickets(pageable, null, "Saints-Petersburg",
                "Moscow", "J7");

        assertFalse(allAccessibleTickets.isEmpty());
        assertEquals(pageSize, allAccessibleTickets.size());
        assertEquals("Saints-Petersburg", allAccessibleTickets.getFirst().getDeparturePoint());
        assertEquals("Moscow", allAccessibleTickets.getFirst().getDestinationPoint());
        assertEquals("J7", allAccessibleTickets.getFirst().getCarrierName());
    }

    @Test
    public void testFindAllAccessibleTicketsWithoutParams() {
        List<TicketWithRouteDataDTO> allAccessibleTickets = ticketService.findAllAccessibleTickets();

        List<Object> objects = new ArrayList<>();
        for (TicketWithRouteDataDTO ticket : allAccessibleTickets) {
            if (ticket.getUserId() == null) objects.add(ticket);
        }

        assertFalse(allAccessibleTickets.isEmpty());
        assertEquals(objects.size(), allAccessibleTickets.size());
        assertEquals(objects, allAccessibleTickets);
    }

    @Test
    public void testSave() {
        TicketCreateDTO createDTO = TicketFactory.createTicketCreateDTO();

        TicketDTO savedTicket = ticketService.saveTicket(createDTO);

        assertTrue(savedTicket.getId() != null && savedTicket.getId() > 0);
        assertThrows(TicketSaveException.class, () -> ticketService.saveTicket(null));
    }
    @Test
    public void testUpdate() {
        TicketDTO ticketDTOFromDb = ticketService.findTicketById(idOfSavedTicket);

        TicketUpdateDTO updateDTO = new TicketUpdateDTO();
        updateDTO.setDateTime(JsonNullable.of(LocalDateTime.now()));
        updateDTO.setPrice(JsonNullable.of(new BigDecimal("567.78")));

        ticketService.updateTicket(updateDTO, idOfSavedTicket);
        TicketDTO updatedTicketDto = ticketService.findTicketById(idOfSavedTicket);

        assertEquals(updatedTicketDto.getUserId(), ticketDTOFromDb.getUserId() );
        assertNotEquals(updatedTicketDto.getDateTime(), ticketDTOFromDb.getDateTime());
        assertEquals(updatedTicketDto.getRouteId(), ticketDTOFromDb.getRouteId());
        assertEquals(updatedTicketDto.getUserId(), ticketDTOFromDb.getUserId());
        assertNotEquals(updatedTicketDto.getPrice(), ticketDTOFromDb.getPrice());
        assertEquals(updatedTicketDto.getSeatNumber(), ticketDTOFromDb.getSeatNumber());
    }

    @Test
    public void testDeleteTicket() {
        ticketService.deleteTicket(idOfSavedTicket);

        assertThrows(TicketNotFoundException.class, () -> ticketService.findTicketById(idOfSavedTicket));
    }
    @Test
    public void testPurchaseTicket() { //FIX

        ticketService.purchaseTicket(userId, availableTicketId);

        List<TicketDTO> tickets = ticketService.findAllPurchasedTickets(userId);
        assertFalse(tickets.isEmpty());
        assertEquals(1, tickets.size());
        assertEquals(tickets.getFirst().getUserId(), userId);
    }
}
