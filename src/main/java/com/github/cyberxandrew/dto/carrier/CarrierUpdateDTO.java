package com.github.cyberxandrew.dto.carrier;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class CarrierUpdateDTO {
    @NotBlank
    private JsonNullable<String> name;
    private JsonNullable<String> phoneNumber;
}
