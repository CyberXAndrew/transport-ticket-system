package com.github.cyberxandrew.dto.carrier;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CarrierCreateDTO {
    @NotBlank
    private String name;
    private String phoneNumber;
}
