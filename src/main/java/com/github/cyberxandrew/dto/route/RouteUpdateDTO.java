package com.github.cyberxandrew.dto.route;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RouteUpdateDTO {
    private String departurePoint;
    private String destinationPoint;
    private Long carrierId;
    @Min(1)
    private Integer duration;
}
