package com.github.cyberxandrew.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Carrier {
    private Long id;
    @NotBlank
    private String name;
    @Min(6)
    private String phoneNumber;
}
