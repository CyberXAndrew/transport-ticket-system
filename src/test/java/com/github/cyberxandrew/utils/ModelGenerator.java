package com.github.cyberxandrew.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.github.cyberxandrew.dto.TicketCreateDTO;
import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.dto.TicketUpdateDTO;
import com.github.cyberxandrew.dto.TicketWithRouteDataDTO;
import com.github.cyberxandrew.model.Ticket;

public class ModelGenerator {

    private static Long testTicketId;
    private static Long testUserId;
    private static Long testRouteId;
    private static BigDecimal testPrice;
    private static String testSeatNumber;
    private static String testDeparturePoint;
    private static String testDestinationPoint;
    private static String testCarrierName;
    private static Ticket testTicket;

    static {
        testTicketId = 1L;
        testUserId = 2L;
        testRouteId = 3L;
        testPrice = new BigDecimal("123.45");
        testSeatNumber = "1A";

        testDeparturePoint = "Saints-Petersburg";
        testDestinationPoint = "Moscow";
        testCarrierName = "Java Airlines";
    }

    public static Ticket createTicketToSave() {
        Ticket ticket = new Ticket();

        ticket.setDateTime(LocalDateTime.now());
        ticket.setUserId(testUserId);
        ticket.setRouteId(testRouteId);
        ticket.setPrice(testPrice);
        ticket.setSeatNumber(testSeatNumber);

        return ticket;
    }

    public static TicketDTO createTicketDTO() {
        TicketDTO ticketDTO = new TicketDTO();

        ticketDTO.setId(testTicketId);
        ticketDTO.setDateTime(LocalDateTime.now());
        ticketDTO.setUserId(testUserId);
        ticketDTO.setRouteId(testRouteId);
        ticketDTO.setPrice(testPrice);
        ticketDTO.setSeatNumber(testSeatNumber);

        return ticketDTO;
    }

    public static TicketCreateDTO createTicketCreateDTO() {
        TicketCreateDTO ticketCreateDTO = new TicketCreateDTO();

        ticketCreateDTO.setDateTime(LocalDateTime.now());
        ticketCreateDTO.setUserId(testUserId);
        ticketCreateDTO.setRouteId(testRouteId);
        ticketCreateDTO.setPrice(testPrice);
        ticketCreateDTO.setSeatNumber(testSeatNumber);

        return ticketCreateDTO;
    }

    public static TicketUpdateDTO createTicketUpdateDTO() {
        TicketUpdateDTO updateDTO = new TicketUpdateDTO();

        updateDTO.setDateTime(LocalDateTime.now());
        updateDTO.setUserId(testUserId);
        updateDTO.setRouteId(testRouteId);
        updateDTO.setPrice(testPrice);
        updateDTO.setSeatNumber(testSeatNumber);

        return updateDTO;
    }

    public static void setTicketFieldsWithoutId(Ticket ticket) {
        ticket.setDateTime(LocalDateTime.now());
        ticket.setUserId(testUserId);
        ticket.setRouteId(testRouteId);
        ticket.setPrice(testPrice);
        ticket.setSeatNumber(testSeatNumber);
    }

    public static void setTicketWithRouteDataDtoFieldsWithoutUserId(TicketWithRouteDataDTO ticketWithRouteDataDto) {
        ticketWithRouteDataDto.setId(testTicketId);
        ticketWithRouteDataDto.setDateTime(LocalDateTime.now());
        ticketWithRouteDataDto.setUserId(null);
        ticketWithRouteDataDto.setRouteId(testRouteId);
        ticketWithRouteDataDto.setPrice(testPrice);
        ticketWithRouteDataDto.setSeatNumber(testSeatNumber);

        ticketWithRouteDataDto.setDeparturePoint(testDeparturePoint);
        ticketWithRouteDataDto.setDestinationPoint(testDestinationPoint);
        ticketWithRouteDataDto.setCarrierName(testCarrierName);
    }
}
