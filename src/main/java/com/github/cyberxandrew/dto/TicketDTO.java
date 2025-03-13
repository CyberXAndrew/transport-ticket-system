package com.github.cyberxandrew.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketDTO {
    private Long id;
    private LocalDateTime dateTime;
    private Long userId;
    private Long routeId;
    private BigDecimal price;
    private String seatNumber;

    private String departurePoint;
    private String destinationPoint;
    private String carrierName;
}
