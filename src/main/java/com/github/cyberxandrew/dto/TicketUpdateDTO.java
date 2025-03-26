package com.github.cyberxandrew.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketUpdateDTO {
    private JsonNullable<LocalDateTime> dateTime;
    private JsonNullable<Long> userId;
    private JsonNullable<Long> routeId;
    @DecimalMin("0.00")
    private JsonNullable<BigDecimal> price;
    private JsonNullable<String> seatNumber;
}
