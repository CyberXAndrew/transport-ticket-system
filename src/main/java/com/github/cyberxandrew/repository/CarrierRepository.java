package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.dto.CarrierDTO;
import com.github.cyberxandrew.model.Carrier;

import java.util.List;
import java.util.Optional;

public interface CarrierRepository {
    Optional<Carrier> findById(Long id);
    List<Carrier> findAll();
    Carrier saveCarrier(Carrier carrier);
    Carrier updateCarrier(Carrier carrier);
    void deleteById(Long id);
}
