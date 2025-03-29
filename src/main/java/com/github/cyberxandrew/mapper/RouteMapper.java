package com.github.cyberxandrew.mapper;

import com.github.cyberxandrew.dto.route.RouteCreateDTO;
import com.github.cyberxandrew.dto.route.RouteDTO;
import com.github.cyberxandrew.dto.route.RouteUpdateDTO;
import com.github.cyberxandrew.model.Route;
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
public interface RouteMapper {
        RouteDTO routeToRouteDTO(Route route);
        Route routeCreateDTOToRoute(RouteCreateDTO createDTO);
        RouteDTO routeCreateDTOToRouteDTO(RouteCreateDTO createDTO);
        RouteDTO routeUpdateDTOToRouteDTO(RouteUpdateDTO updateDTO);
        RouteUpdateDTO routeDTOToUpdateDTO(RouteDTO routeDTO);
        @Mapping(target = "id", ignore = true)
        void update(RouteUpdateDTO updateDTO, @MappingTarget Route route);
}
