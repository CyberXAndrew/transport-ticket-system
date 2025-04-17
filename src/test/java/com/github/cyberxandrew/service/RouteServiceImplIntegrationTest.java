package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.route.RouteCreateDTO;
import com.github.cyberxandrew.dto.route.RouteDTO;
import com.github.cyberxandrew.dto.route.RouteUpdateDTO;
import com.github.cyberxandrew.exception.route.RouteHasTicketsException;
import com.github.cyberxandrew.exception.route.RouteNotFoundException;
import com.github.cyberxandrew.exception.route.RouteSaveException;
import com.github.cyberxandrew.utils.RouteFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class RouteServiceImplIntegrationTest {
    @Autowired private RouteServiceImpl routeService;

    private Long absentId;
    private Long idOfSavedRoute;
    private Long idOfBoundedWithTicketsRoute;
    private String departurePoint;
    private String destinationPoint;
    private Long carrierId;
    private Integer duration;

    @BeforeEach
    public void setUp() {
        absentId = 999L;
        idOfSavedRoute = 1L;
        idOfBoundedWithTicketsRoute = 5L;
        departurePoint = "NY";
        destinationPoint = "Madrid";
        carrierId = 2L;
        duration = 150;
    }

    @Test
    public void findRouteById() {
        RouteCreateDTO createDTO = RouteFactory.createRouteCreateDTO();
        RouteDTO savedRouteDto = routeService.saveRoute(createDTO);

        RouteDTO routeDto = routeService.findRouteById(savedRouteDto.getId());

        assertEquals(routeDto, savedRouteDto);
        assertThrows(RouteNotFoundException.class, () -> routeService.findRouteById(absentId));
    }

    @Test
    public void findAll() {
        List<RouteDTO> allRoutes = routeService.findAll();

        assertFalse(allRoutes.isEmpty());
    }

    @Test
    public void saveRoute() {
        RouteCreateDTO createDTO = RouteFactory.createRouteCreateDTO();

        RouteDTO savedRoute = routeService.saveRoute(createDTO);

        assertTrue(savedRoute != null && savedRoute.getId() > 0);
        assertThrows(RouteSaveException.class, () -> routeService.saveRoute(null));
    }

    @Test
    public void updateRoute() {
        RouteDTO routeDTOFromDb = routeService.findRouteById(idOfSavedRoute);

        RouteUpdateDTO updateDTO = new RouteUpdateDTO();
        updateDTO.setDeparturePoint(JsonNullable.of(departurePoint));
        updateDTO.setDestinationPoint(JsonNullable.of(destinationPoint));
        updateDTO.setCarrierId(JsonNullable.of(carrierId));
        updateDTO.setDuration(JsonNullable.of(duration));

        routeService.updateRoute(updateDTO, idOfSavedRoute);
        RouteDTO updatedRouteDto = routeService.findRouteById(idOfSavedRoute);

        assertEquals(updatedRouteDto.getId(), routeDTOFromDb.getId());
        assertNotEquals(updatedRouteDto.getDeparturePoint(), routeDTOFromDb.getDeparturePoint());
        assertNotEquals(updatedRouteDto.getDestinationPoint(), routeDTOFromDb.getDestinationPoint());
        assertNotEquals(updatedRouteDto.getCarrierId(), routeDTOFromDb.getCarrierId());
        assertNotEquals(updatedRouteDto.getDuration(), routeDTOFromDb.getDuration());
    }

    @Test
    public void deleteBoundedWithTicketsRoute() {
        assertThrows(RouteHasTicketsException.class, () -> routeService.deleteRoute(idOfBoundedWithTicketsRoute));
    }

    @Test
    public void deleteRoute() {
        RouteCreateDTO createDTO = new RouteCreateDTO();
        createDTO.setDeparturePoint(departurePoint);
        createDTO.setDestinationPoint(destinationPoint);
        createDTO.setCarrierId(carrierId);
        createDTO.setDuration(duration);

        RouteDTO savedRouteDTO = routeService.saveRoute(createDTO);

        assertEquals(savedRouteDTO.getDeparturePoint(), createDTO.getDeparturePoint());

        routeService.deleteRoute(savedRouteDTO.getId());

        assertThrows(RouteNotFoundException.class, () -> routeService.findRouteById(savedRouteDTO.getId()));
    }
}
