package com.github.cyberxandrew.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CarrierDTO {
    private Long id;
    private String name;
    @Min(6)
    private String phoneNumber;
}
