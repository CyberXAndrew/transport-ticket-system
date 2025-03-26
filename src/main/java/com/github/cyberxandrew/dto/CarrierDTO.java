package com.github.cyberxandrew.dto;

import jakarta.validation.constraints.Min;

public class CarrierDTO {
    private String name;
    @Min(6)
    private String phoneNumber;
}
