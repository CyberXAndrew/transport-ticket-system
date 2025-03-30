package com.github.cyberxandrew.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cyberxandrew.config.JacksonConfig;
import com.github.cyberxandrew.dto.route.RouteCreateDTO;
import com.github.cyberxandrew.dto.route.RouteDTO;
import com.github.cyberxandrew.dto.route.RouteUpdateDTO;
import com.github.cyberxandrew.mapper.RouteMapper;
import com.github.cyberxandrew.mapper.RouteMapperImpl;
import com.github.cyberxandrew.mapper.JsonNullableMapperImpl;
import com.github.cyberxandrew.model.Route;
import com.github.cyberxandrew.service.RouteServiceImpl;
import com.github.cyberxandrew.utils.RouteFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RouteController.class)
@Import({RouteMapperImpl.class, JsonNullableMapperImpl.class, JacksonConfig.class})
@ActiveProfiles("test")
class RouteControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RouteMapper routeMapper;
    @MockitoBean private RouteServiceImpl routeService;
    private Long routeId1;
    private Long routeId2;
    private String departurePoint1;
    private String departurePoint2;
    private String destinationPoint1;
    private String destinationPoint2;
    private Long carrierId1;
    private Long carrierId2;
    private Integer duration1;
    private Integer duration2;

    
    @BeforeEach
    void SetUp() {
        routeId1 = 1L;
        routeId2 = 2L;
        departurePoint1 = "Paris";
        departurePoint2 = "Moscow";
        destinationPoint1 = "Vienna";
        destinationPoint2 = "Los-Angeles";
        carrierId1 = 3L;
        carrierId2 = 4L;
        duration1 = 60;
        duration2 = 50;
    }

    @Test
    public void testShow() throws Exception {
        RouteDTO routeDTO = RouteFactory.createRouteDTO();
        routeDTO.setId(routeId1);

        when(routeService.findRouteById(routeId1)).thenReturn(routeDTO);

        mockMvc.perform(get("/api/routes/{id}", routeId1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(routeDTO)));
    }

    @Test
    public void testIndex() throws Exception {
        RouteFactory.RouteBuilder routeBuilder = new RouteFactory.RouteBuilder(); //fixme Took out to factory

        Route route1 = routeBuilder.withRouteId(routeId1)
                .withRouteDeparturePoint(departurePoint1)
                .withRouteDestinationPoint(destinationPoint1)
                .withRouteCarrierId(carrierId1)
                .withRouteDuration(duration1).build();
        RouteDTO routeDTO1 = routeMapper.routeToRouteDTO(route1);

        Route route2 = routeBuilder.withRouteId(routeId2)
                .withRouteDeparturePoint(departurePoint2)
                .withRouteDestinationPoint(destinationPoint2)
                .withRouteCarrierId(carrierId2)
                .withRouteDuration(duration2).build();
        RouteDTO routeDTO2 = routeMapper.routeToRouteDTO(route2);

        List<RouteDTO> expectedList = List.of(routeDTO1, routeDTO2);

        when(routeService.findAll()).thenReturn(expectedList);

        mockMvc.perform(get("/api/routes"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", is(String.valueOf(expectedList.size()))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
    }

    @Test
    public void testCreate() throws Exception {
        RouteCreateDTO routeCreateDTO = RouteFactory.createRouteCreateDTO();
        RouteDTO routeDTO = routeMapper.routeCreateDTOToRouteDTO(routeCreateDTO);
        routeDTO.setId(routeId1);

        when(routeService.saveRoute(routeCreateDTO)).thenReturn(routeDTO);

        mockMvc.perform(post("/api/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routeCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(routeDTO)));
    }

    @Test
    public void testUpdate() throws Exception {
        RouteUpdateDTO routeUpdateDTO = RouteFactory.createRouteUpdateDTO();
        RouteDTO routeDTO = routeMapper.routeUpdateDTOToRouteDTO(routeUpdateDTO);
        routeDTO.setId(routeId1);

        when(routeService.updateRoute(routeUpdateDTO, routeId1)).thenReturn(routeDTO);

        mockMvc.perform(put("/api/routes/{id}", routeId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routeUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(routeDTO)));
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(routeService).deleteRoute(routeId1);

        mockMvc.perform(delete("/api/routes/{id}", routeId1))
                .andExpect(status().isNoContent());
    }
}