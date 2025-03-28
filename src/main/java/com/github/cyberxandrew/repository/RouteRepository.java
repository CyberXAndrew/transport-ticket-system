package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.dto.RouteDTO;
import com.github.cyberxandrew.model.Route;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RouteRepository {
    Optional<Route> findById(Long routeId);
    List<Route> findAll();
    Route save(Route route);
    Route update(Route route);
    void deleteById(Long routeId);
}
