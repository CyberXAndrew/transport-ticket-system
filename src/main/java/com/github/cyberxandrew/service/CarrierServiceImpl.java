package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.CarrierCreateDTO;
import com.github.cyberxandrew.dto.CarrierDTO;
import com.github.cyberxandrew.dto.CarrierUpdateDTO;
import com.github.cyberxandrew.exception.carrier.CarrierNotFoundException;
import com.github.cyberxandrew.exception.carrier.CarrierSaveException;
import com.github.cyberxandrew.exception.carrier.CarrierUpdateException;
import com.github.cyberxandrew.mapper.CarrierMapper;
import com.github.cyberxandrew.model.Carrier;
import com.github.cyberxandrew.repository.CarrierRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarrierServiceImpl implements CarrierService {
    @Autowired private CarrierMapper carrierMapper;
    @Autowired private CarrierRepositoryImpl carrierRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public CarrierDTO findCarrierById(Long carrierId) {
        return carrierMapper.carrierToCarrierDTO(carrierRepository.findById(carrierId).orElseThrow(
                () -> new CarrierNotFoundException("Carrier with id" + carrierId + " not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarrierDTO> findAll() {
        return carrierRepository.findAll().stream()
                .map(carrier -> carrierMapper.carrierToCarrierDTO(carrier))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CarrierDTO saveCarrier(CarrierCreateDTO createDTO) {
        if (createDTO == null) throw new CarrierSaveException("Error while saving Carrier: Carrier to save is null");
        Carrier carrier = carrierMapper.carrierCreateDTOToCarrier(createDTO);
        return carrierMapper.carrierToCarrierDTO(carrierRepository.save(carrier));
    }

    @Override
    @Transactional
    public CarrierDTO updateCarrier(CarrierUpdateDTO updateDTO, Long id) {
        Carrier carrier = carrierRepository.findById(id).orElseThrow(() ->
                new CarrierUpdateException("Error while updating Carrier: Carrier by id is null"));

        carrierMapper.update(updateDTO, carrier);
        return carrierMapper.carrierToCarrierDTO(carrierRepository.update(carrier));
    }

    @Override
    @Transactional
    public void deleteCarrier(Long carrierId) {
        carrierRepository.deleteById(carrierId);
    }
}


