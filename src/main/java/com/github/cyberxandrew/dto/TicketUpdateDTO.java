package com.github.cyberxandrew.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketUpdateDTO {
//    @NotNull
//    private Long id;
    @NotNull
    private LocalDateTime dateTime;

    private Long userId;
    @NotNull
    private Long routeId;
    @NotNull
    private BigDecimal price;
    @NotNull
    private String seatNumber;
}
