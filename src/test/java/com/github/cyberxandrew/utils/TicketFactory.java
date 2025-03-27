package com.github.cyberxandrew.utils;

import com.github.cyberxandrew.dto.TicketCreateDTO;
import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.dto.TicketUpdateDTO;
import com.github.cyberxandrew.dto.TicketWithRouteDataDTO;
import com.github.cyberxandrew.model.Carrier;
import com.github.cyberxandrew.model.Ticket;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TicketFactory {

    private static Long testTicketId;
    private static Long testUserId;
    private static Long testRouteId;
    private static BigDecimal testPrice;
    private static String testSeatNumber;
    private static String testDeparturePoint;
    private static String testDestinationPoint;
    private static String testCarrierName;

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

        updateDTO.setDateTime(JsonNullable.of(LocalDateTime.now()));
        updateDTO.setUserId(JsonNullable.of(testUserId));
        updateDTO.setRouteId(JsonNullable.of(testRouteId));
        updateDTO.setPrice(JsonNullable.of(testPrice));
        updateDTO.setSeatNumber(JsonNullable.of(testSeatNumber));

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

    public static void setCarrierFieldsWithoutId(Carrier carrier) {
        carrier.setName("");
    }

    public static class TicketBuilder {
        private Long id = 1L;
        private LocalDateTime dateTime = LocalDateTime.now();
        private Long userId = null;
        private Long routeId = 2L;
        private BigDecimal price = new BigDecimal(123.45);
        private String seatNumber = "1A";

        public TicketBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public TicketBuilder withDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public TicketBuilder withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public TicketBuilder withRouteId(Long routeId) {
            this.routeId = routeId;
            return this;
        }

        public TicketBuilder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public TicketBuilder withSeatNumber(String seatNumber) {
            this.seatNumber = seatNumber;
            return this;
        }

        public Ticket build() {
            Ticket ticket = new Ticket();
            ticket.setId(id);
            ticket.setDateTime(dateTime);
            ticket.setUserId(userId);
            ticket.setRouteId(routeId);
            ticket.setPrice(price);
            ticket.setSeatNumber(seatNumber);
            return ticket;
        }
    }
}
