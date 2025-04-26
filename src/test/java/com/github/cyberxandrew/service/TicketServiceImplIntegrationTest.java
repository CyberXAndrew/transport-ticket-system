package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.ticket.TicketCreateDTO;
import com.github.cyberxandrew.dto.ticket.TicketDTO;
import com.github.cyberxandrew.dto.ticket.TicketUpdateDTO;
import com.github.cyberxandrew.dto.ticket.TicketWithRouteDataDTO;
import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.exception.ticket.TicketSaveException;
import com.github.cyberxandrew.mapper.TicketMapper;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.utils.KafkaTestListener;
import com.github.cyberxandrew.utils.TicketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@Transactional
@ActiveProfiles("test")
@DirtiesContext
@TestPropertySource(properties = { "spring.kafka.bootstrap-servers:localhost:9092" })
public class TicketServiceImplIntegrationTest {
    @Autowired private TicketServiceImpl ticketService;
    @Autowired private TicketCacheService ticketCacheService;
    @Autowired private KafkaTemplate<String, Ticket> kafkaTemplate; // fix Ticket?
    @Autowired private KafkaTestListener kafkaTestListener;
    @Autowired private TicketMapper ticketMapper;
    private Long testAbsentId;
    private Long availableTicketId;
    private Long unavailableTicketId;
    private Long idOfSavedTicket;
    private Long userId;
    private Long routeId;
    private String seatNumber;

//    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        testAbsentId = -1L;
        idOfSavedTicket = 1L;
        availableTicketId = 1L;
        unavailableTicketId = 4L;
        userId = 2L;
        routeId = 3L;
        seatNumber = "1C";

        kafkaTestListener.reset();
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
        Ticket ticketToPurchase = new TicketFactory.TicketBuilder()
                .withDateTime(LocalDateTime.now())
                .withUserId(null)
                .withRouteId(routeId)
                .withPrice(new BigDecimal("123.00"))
                .withSeatNumber(seatNumber)
                .build();
        TicketCreateDTO ticketDTO = ticketMapper.ticketToTicketCreateDTO(ticketToPurchase);
        TicketDTO savedTicketDTO = ticketService.saveTicket(ticketDTO);
        Long savedTicketId = ticketService.findTicketById(savedTicketDTO.getId()).getId();
        ticketCacheService.evictPurchasedTickets(userId, 0, -1);
        ticketService.purchaseTicket(userId, savedTicketId);

        List<TicketDTO> ticketList = ticketService.findAllPurchasedTickets(userId);

        assertFalse(ticketList.isEmpty());
        assertEquals(1, ticketList.size());
        assertEquals(ticketList.getFirst().getId(), savedTicketId);

        ticketCacheService.evictPurchasedTickets(userId, 0, -1);
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
    public void testPurchaseTicket() throws InterruptedException {
//        System.out.println("======[KAFKA LISTENER]:\n" + kafkaTestListener.getCountDownLatch() + "\n------");// TEST TEMPORARY STRING
        System.out.println("======[KAFKA LISTENER]:\n" + kafkaTestListener.getMessageReceived() + "\n------");// TEST TEMPORARY STRING
        System.out.println("======[KAFKA LISTENER]:\n" + kafkaTestListener.getReceivedMessage() + "\n------");// TEST TEMPORARY STRING

        ticketService.purchaseTicket(userId, availableTicketId);

        List<TicketDTO> tickets = ticketService.findAllPurchasedTickets(userId);
        assertFalse(tickets.isEmpty());
        assertEquals(1, tickets.size());
        assertEquals(tickets.getFirst().getUserId(), userId);

//        boolean messageConsumed = kafkaTestListener.getCountDownLatch().await(10, TimeUnit.SECONDS);
        long startTime = System.currentTimeMillis();
        long awaitTime = 10000;

        System.out.println("======[ATOMIC ]:\n" + kafkaTestListener.getMessageReceived().get() + "\n------");// TEST TEMPORARY STRING
        int i = 0;
        while (!kafkaTestListener.getMessageReceived().get() && (System.currentTimeMillis() - startTime) < awaitTime) {
            System.out.println("======[WHILE]:\n" + kafkaTestListener.getMessageReceived().get() + "\n------");// TEST TEMPORARY STRING
            i++;
            Thread.sleep(1000);
            Thread.yield();
        }
        System.out.println("======[i]:\n" + i + "\n------");// TEST TEMPORARY STRING

        Ticket expectedMessage = ticketMapper.ticketDTOToTicket(tickets.getFirst());
//        assertTrue(messageConsumed, "Message was not consumed by kafka listener");
        System.out.println("======[received message]:\n" + kafkaTestListener.getReceivedMessage() + "\n------");// TEST TEMPORARY STRING
        assertNotNull(kafkaTestListener.getReceivedMessage(), "Received message is null");
        assertEquals(expectedMessage, kafkaTestListener.getReceivedMessage(), "Received message does not" +
                " match expected ticket");
    }
}
