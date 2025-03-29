package com.github.cyberxandrew.dto.route;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RouteCreateDTO {
    @NotBlank
    private String departurePoint;
    @NotBlank
    private String destinationPoint;
    @NotNull
    private Long carrierId;
    @Min(1)
    private Integer duration;
}
