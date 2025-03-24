package com.github.cyberxandrew.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketDTO {
    private Long id;
    private LocalDateTime dateTime;
    private Long userId;
    private Long routeId;
//    @DecimalMin("0.00")
    private BigDecimal price;
    private String seatNumber;
}
