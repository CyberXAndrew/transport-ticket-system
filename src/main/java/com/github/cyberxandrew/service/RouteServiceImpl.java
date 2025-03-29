package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.route.RouteCreateDTO;
import com.github.cyberxandrew.dto.route.RouteDTO;
import com.github.cyberxandrew.dto.route.RouteUpdateDTO;
import com.github.cyberxandrew.exception.route.RouteNotFoundException;
import com.github.cyberxandrew.exception.route.RouteSaveException;
import com.github.cyberxandrew.exception.route.RouteUpdateException;
import com.github.cyberxandrew.mapper.RouteMapper;
import com.github.cyberxandrew.model.Route;
import com.github.cyberxandrew.repository.RouteRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {
    @Autowired private RouteMapper routeMapper;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private RouteRepositoryImpl routeRepository;

    @Override
    @Transactional(readOnly = true)
    public RouteDTO findRouteById(Long routeId) {
        return routeMapper.routeToRouteDTO(routeRepository.findById(routeId).orElseThrow(
                () -> new RouteNotFoundException("Route with id" + routeId + " not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteDTO> findAll() {
        return routeRepository.findAll().stream()
                .map(route -> routeMapper.routeToRouteDTO(route))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RouteDTO saveRoute(RouteCreateDTO createDTO) {
        if (createDTO == null) throw new RouteSaveException("Error while saving Route: Route to save is null");
        Route route = routeMapper.routeCreateDTOToRoute(createDTO);
        return routeMapper.routeToRouteDTO(routeRepository.save(route));
    }

    @Override
    @Transactional
    public RouteDTO updateRoute(RouteUpdateDTO updateDTO, Long id) {
        Route route = routeRepository.findById(id).orElseThrow(() ->
                new RouteUpdateException("Error while updating Route: Route by id is null"));

        routeMapper.update(updateDTO, route);
        return routeMapper.routeToRouteDTO(routeRepository.update(route));
    }

    @Override
    @Transactional
    public void deleteRoute(Long routeId) {
        routeRepository.deleteById(routeId);
    }
}
