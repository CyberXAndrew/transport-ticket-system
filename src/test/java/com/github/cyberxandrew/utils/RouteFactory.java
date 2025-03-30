package com.github.cyberxandrew.utils;

import com.github.cyberxandrew.dto.route.RouteCreateDTO;
import com.github.cyberxandrew.dto.route.RouteDTO;
import com.github.cyberxandrew.dto.route.RouteUpdateDTO;
import com.github.cyberxandrew.model.Route;

public class RouteFactory {
    private static Long id = 1L;
    private static String departurePoint = "testDeparturePoint";
    private static String destinationPoint = "testDestinationPoint";
    private static Long carrierId = 2L;
    private static Integer duration = 60;

    public static Route createRouteToSave() {
        Route route = new Route();
        route.setDeparturePoint(departurePoint);
        route.setDestinationPoint(destinationPoint);
        route.setCarrierId(carrierId);
        route.setDuration(duration);
        return route;
    }

    public static RouteDTO createRouteDTO() {
        RouteDTO routeDTO = new RouteDTO();

        routeDTO.setId(id);
        routeDTO.setDeparturePoint(departurePoint);
        routeDTO.setDestinationPoint(destinationPoint);
        routeDTO.setCarrierId(carrierId);
        routeDTO.setDuration(duration);

        return routeDTO;
    }

    public static RouteCreateDTO createRouteCreateDTO() {
        RouteCreateDTO routeCreateDTO = new RouteCreateDTO();

        routeCreateDTO.setDeparturePoint(departurePoint);
        routeCreateDTO.setDestinationPoint(destinationPoint);
        routeCreateDTO.setCarrierId(carrierId);
        routeCreateDTO.setDuration(duration);

        return routeCreateDTO;
    }

    public static RouteUpdateDTO createRouteUpdateDTO() {
        RouteUpdateDTO routeUpdateDTO = new RouteUpdateDTO();

        routeUpdateDTO.setDeparturePoint(departurePoint);
        routeUpdateDTO.setDestinationPoint(destinationPoint);
        routeUpdateDTO.setCarrierId(carrierId);
        routeUpdateDTO.setDuration(duration);

        return routeUpdateDTO;
    }

    public static class RouteBuilder {
        private Long id = 1L;
        private String departurePoint = "testDeparturePoint";
        private String destinationPoint = "testDestinationPoint";
        private Long carrierId = 2L;
        private Integer duration = 60;

        public RouteFactory.RouteBuilder withRouteId (Long id) {
            this.id = id;
            return this;
        }

        public RouteFactory.RouteBuilder withRouteDeparturePoint(String departurePoint) {
            this.departurePoint = departurePoint;
            return this;
        }

        public RouteFactory.RouteBuilder withRouteDestinationPoint(String destinationPoint) {
            this.destinationPoint = destinationPoint;
            return this;
        }

        public RouteFactory.RouteBuilder withRouteCarrierId(Long carrierId) {
            this.carrierId = carrierId;
            return this;
        }

        public RouteFactory.RouteBuilder withRouteDuration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public Route build() {
            Route route = new Route();
            route.setId(id);
            route.setDeparturePoint(departurePoint);
            route.setDestinationPoint(destinationPoint);
            route.setCarrierId(carrierId);
            route.setDuration(duration);
            return route;
        }
    }
}
