package com.github.cyberxandrew.controller;

import com.github.cyberxandrew.dto.route.RouteCreateDTO;
import com.github.cyberxandrew.dto.route.RouteDTO;
import com.github.cyberxandrew.dto.route.RouteUpdateDTO;
import com.github.cyberxandrew.service.RouteServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
@RequestMapping(path = "/api/routes")
public class RouteController {
    @Autowired private RouteServiceImpl routeService;

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RouteDTO show(@PathVariable Long id) {
        return routeService.findRouteById(id);
    }

    @GetMapping(path = "")
    public ResponseEntity<List<RouteDTO>> index() {
        List<RouteDTO> allRoutes = routeService.findAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(allRoutes.size()))
                .body(allRoutes);
    }

    @Secured({ "ROLE_ADMIN" })
    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public RouteDTO create(@Valid @RequestBody RouteCreateDTO createDTO) {
        return routeService.saveRoute(createDTO);
    }

    @Secured({ "ROLE_ADMIN" })
    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RouteDTO update(@Valid @RequestBody RouteUpdateDTO updateDTO, @PathVariable Long id) {
        return routeService.updateRoute(updateDTO, id);
    }

    @Secured({ "ROLE_ADMIN" })
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        routeService.deleteRoute(id);
    }
}
