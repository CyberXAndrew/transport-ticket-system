package com.github.cyberxandrew.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class Ticket {
    private Long id;
//    @Future
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
