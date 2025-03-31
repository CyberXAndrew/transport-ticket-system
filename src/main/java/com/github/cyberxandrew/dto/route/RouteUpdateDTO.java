package com.github.cyberxandrew.dto.route;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class RouteUpdateDTO {
    private JsonNullable<String> departurePoint;
    private JsonNullable<String> destinationPoint;
    private JsonNullable<Long> carrierId;
    @Min(1)
    private JsonNullable<Integer> duration;
}
