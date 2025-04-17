package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.carrier.CarrierCreateDTO;
import com.github.cyberxandrew.dto.carrier.CarrierDTO;
import com.github.cyberxandrew.dto.carrier.CarrierUpdateDTO;
import com.github.cyberxandrew.exception.carrier.CarrierHasRoutesException;
import com.github.cyberxandrew.exception.carrier.CarrierNotFoundException;
import com.github.cyberxandrew.exception.carrier.CarrierSaveException;
import com.github.cyberxandrew.utils.CarrierFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class CarrierServiceImplIntegrationTest {
    @Autowired private CarrierServiceImpl carrierService;

    private Long absentId;
    private Long idOfSavedCarrier;
    private String name;
    private String phoneNumber;

    @BeforeEach
    public void setUp() {
        absentId = 999L;
        idOfSavedCarrier = 1L;
        name = "test carrier name";
        phoneNumber = "123-456-7890";
    }
    
    @Test
    public void findCarrierById() {
        CarrierCreateDTO createDTO = CarrierFactory.createCarrierCreateDTO();
        CarrierDTO savedCarrierDto = carrierService.saveCarrier(createDTO);

        CarrierDTO carrierDto = carrierService.findCarrierById(savedCarrierDto.getId());

        assertEquals(carrierDto, savedCarrierDto);
        assertThrows(CarrierNotFoundException.class, () -> carrierService.findCarrierById(absentId));
    }

    @Test
    public void findAll() {
        List<CarrierDTO> allCarriers = carrierService.findAll();

        assertFalse(allCarriers.isEmpty());
    }

    @Test
    public void saveCarrier() {
        CarrierCreateDTO createDTO = CarrierFactory.createCarrierCreateDTO();

        CarrierDTO savedCarrier = carrierService.saveCarrier(createDTO);

        assertTrue(savedCarrier != null && savedCarrier.getId() > 0);
        assertThrows(CarrierSaveException.class, () -> carrierService.saveCarrier(null));
    }

    @Test
    public void updateCarrier() {
        CarrierDTO carrierDTOFromDb = carrierService.findCarrierById(idOfSavedCarrier);

        CarrierUpdateDTO updateDTO = new CarrierUpdateDTO();
        updateDTO.setName(JsonNullable.of(name));
        updateDTO.setPhoneNumber(JsonNullable.of(phoneNumber));

        carrierService.updateCarrier(updateDTO, idOfSavedCarrier);
        CarrierDTO updatedCarrierDto = carrierService.findCarrierById(idOfSavedCarrier);

        assertEquals(updatedCarrierDto.getId(), carrierDTOFromDb.getId());
        assertNotEquals(updatedCarrierDto.getName(), carrierDTOFromDb.getName());
        assertNotEquals(updatedCarrierDto.getPhoneNumber(), carrierDTOFromDb.getPhoneNumber());
    }

    @Test
    public void deleteBoundedWithRoutesCarrier() {
        assertThrows(CarrierHasRoutesException.class, () -> carrierService.deleteCarrier(idOfSavedCarrier));
    }
    
    @Test
    public void deleteCarrier() {
        CarrierCreateDTO createDTO = new CarrierCreateDTO();
        createDTO.setName(name);

        CarrierDTO savedCarrierDTO = carrierService.saveCarrier(createDTO);

        assertEquals(savedCarrierDTO.getName(), createDTO.getName());

        carrierService.deleteCarrier(savedCarrierDTO.getId());

        assertThrows(CarrierNotFoundException.class, () -> carrierService.findCarrierById(savedCarrierDTO.getId()));
    }
}
