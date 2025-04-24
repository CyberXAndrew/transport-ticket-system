package com.github.cyberxandrew.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Ticket {
    private Long id;
    @NotNull
    private LocalDateTime dateTime;
    private Long userId;
    @NotNull
    private Long routeId;
    @DecimalMin("0.00")
    @NotNull
    private BigDecimal price;
    @NotBlank
    private String seatNumber;
}
