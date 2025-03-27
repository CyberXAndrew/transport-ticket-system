package com.github.cyberxandrew.mapper;

import com.github.cyberxandrew.dto.CarrierCreateDTO;
import com.github.cyberxandrew.dto.CarrierDTO;
import com.github.cyberxandrew.dto.CarrierUpdateDTO;
import com.github.cyberxandrew.model.Carrier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;


@Mapper(uses = { JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarrierMapper {
    CarrierDTO carrierToCarrierDTO(Carrier carrier);
    Carrier carrierCreateDTOToCarrier(CarrierCreateDTO createDTO);
    CarrierDTO carrierCreateDTOToCarrierDTO(CarrierCreateDTO createDTO);
    CarrierDTO carrierUpdateDTOToCarrierDTO(CarrierUpdateDTO updateDTO);
    CarrierUpdateDTO carrierDTOToUpdateDTO(CarrierDTO carrierDTO);
    @Mapping(target = "id", ignore = true)
    void update(CarrierUpdateDTO updateDTO, @MappingTarget Carrier carrier);
}
