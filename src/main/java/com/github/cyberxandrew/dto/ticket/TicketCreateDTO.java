package com.github.cyberxandrew.dto.ticket;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketCreateDTO {
    @NotNull
    private LocalDateTime dateTime;

    private Long userId;
    @NotNull
    private Long routeId;
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal price;
    @NotBlank
    private String seatNumber;
}
