package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.dto.CarrierDTO;
import com.github.cyberxandrew.model.Carrier;

import java.util.List;
import java.util.Optional;

public interface CarrierRepository {
    Optional<Carrier> findById(Long id);
    List<Carrier> findAll();
    Carrier save(Carrier carrier);
    Carrier update(Carrier carrier);
    void deleteById(Long id);
}
