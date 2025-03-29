package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.carrier.CarrierCreateDTO;
import com.github.cyberxandrew.dto.carrier.CarrierDTO;
import com.github.cyberxandrew.dto.carrier.CarrierUpdateDTO;

import java.util.List;

public interface CarrierService {
    CarrierDTO findCarrierById(Long carrierId);
    List<CarrierDTO> findAll();
    CarrierDTO saveCarrier(CarrierCreateDTO createDTO);
    CarrierDTO updateCarrier(CarrierUpdateDTO updateDTO, Long id);
    void deleteCarrier(Long carrierId);
}
