package com.github.cyberxandrew.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CarrierCreateDTO {
    @NotBlank
    private String name;
    private String phoneNumber;
}
