package com.github.cyberxandrew.controller;

import com.github.cyberxandrew.dto.CarrierCreateDTO;
import com.github.cyberxandrew.dto.CarrierDTO;
import com.github.cyberxandrew.dto.CarrierUpdateDTO;
import com.github.cyberxandrew.service.CarrierServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/carriers")
public class CarrierController {
    @Autowired private CarrierServiceImpl carrierService;

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CarrierDTO show(@PathVariable Long id) {
        return carrierService.findCarrierById(id);
    }

    @GetMapping(path = "")
    public ResponseEntity<List<CarrierDTO>> index() {
        List<CarrierDTO> allCarriers = carrierService.findAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(allCarriers.size()))
                .body(allCarriers);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public CarrierDTO create(@Valid @RequestBody CarrierCreateDTO createDTO) {
        return carrierService.saveCarrier(createDTO);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CarrierDTO update(@Valid @RequestBody CarrierUpdateDTO updateDTO, @PathVariable Long id) {
        return carrierService.updateCarrier(updateDTO, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        carrierService.deleteCarrier(id);
    }
}
