package com.github.cyberxandrew.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Route {
    private Long id;
    @NotBlank
    private String departurePoint;
    @NotBlank
    private String destinationPoint;
    @NotNull
    private Long carrierId;
    @Min(1)
    @NotNull
    private Integer duration;
}
