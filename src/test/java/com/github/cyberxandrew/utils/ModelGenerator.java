package com.github.cyberxandrew.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.github.cyberxandrew.dto.TicketDTO;
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

        ticket.setId(null);
        ticket.setDateTime(LocalDateTime.now());
        ticket.setUserId(testUserId);
        ticket.setRouteId(testRouteId);
        ticket.setPrice(testPrice);
        ticket.setSeatNumber(testSeatNumber);

        return ticket;
    }

    public static void setTicketFieldsWithoutId(Ticket ticket) {
        ticket.setDateTime(LocalDateTime.now());
        ticket.setUserId(testUserId);
        ticket.setRouteId(testRouteId);
        ticket.setPrice(testPrice);
        ticket.setSeatNumber(testSeatNumber);
    }

    public static void setTicketDtoFieldsWithoutUserId(TicketDTO ticketDto) {
        ticketDto.setId(testTicketId);
        ticketDto.setDateTime(LocalDateTime.now());
        ticketDto.setUserId(null);
        ticketDto.setRouteId(testRouteId);
        ticketDto.setPrice(testPrice);
        ticketDto.setSeatNumber(testSeatNumber);

        ticketDto.setDeparturePoint(testDeparturePoint);
        ticketDto.setDestinationPoint(testDestinationPoint);
        ticketDto.setCarrierName(testCarrierName);
    }
}
