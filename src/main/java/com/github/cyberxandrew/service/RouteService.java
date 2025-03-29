package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.route.RouteCreateDTO;
import com.github.cyberxandrew.dto.route.RouteDTO;
import com.github.cyberxandrew.dto.route.RouteUpdateDTO;

import java.util.List;

public interface RouteService {
    RouteDTO findRouteById(Long routeId);
    List<RouteDTO> findAll();
    RouteDTO saveRoute(RouteCreateDTO createDTO);
    RouteDTO updateRoute(RouteUpdateDTO updateDTO, Long id);
    void deleteRoute(Long routeId);
}
