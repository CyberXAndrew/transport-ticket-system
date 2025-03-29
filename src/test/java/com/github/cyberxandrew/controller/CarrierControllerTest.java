package com.github.cyberxandrew.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cyberxandrew.config.JacksonConfig;
import com.github.cyberxandrew.dto.carrier.CarrierCreateDTO;
import com.github.cyberxandrew.dto.carrier.CarrierDTO;
import com.github.cyberxandrew.dto.carrier.CarrierUpdateDTO;
import com.github.cyberxandrew.mapper.CarrierMapper;
import com.github.cyberxandrew.mapper.CarrierMapperImpl;
import com.github.cyberxandrew.mapper.JsonNullableMapperImpl;
import com.github.cyberxandrew.model.Carrier;
import com.github.cyberxandrew.service.CarrierServiceImpl;
import com.github.cyberxandrew.utils.CarrierFactory;
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

@WebMvcTest(CarrierController.class)
@Import({CarrierMapperImpl.class, JsonNullableMapperImpl.class, JacksonConfig.class})
@ActiveProfiles("test")
public class CarrierControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CarrierMapper carrierMapper;
    @MockitoBean private CarrierServiceImpl carrierService;
    private Long carrierId;
    
    @BeforeEach
    void SetUp() {
        carrierId = 1L;
    } 

    @Test
    public void testShow() throws Exception {
        CarrierDTO carrierDTO = CarrierFactory.createCarrierDTO();
        carrierDTO.setId(carrierId);

        when(carrierService.findCarrierById(carrierId)).thenReturn(carrierDTO);

        mockMvc.perform(get("/api/carriers/{id}", carrierId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(carrierDTO)));
    }

    @Test
    public void testIndex() throws Exception {
        CarrierFactory.CarrierBuilder carrierBuilder = new CarrierFactory.CarrierBuilder(); //fixme Took out to factory
        Carrier carrier1 = carrierBuilder.withCarrierId(1L).withCarrierName("name 1").withCarrierPhoneNumber("123").build();
        CarrierDTO carrierDTO1 = carrierMapper.carrierToCarrierDTO(carrier1);
        CarrierFactory.CarrierBuilder carrierBuilder2 = new CarrierFactory.CarrierBuilder();
        Carrier carrier2 = carrierBuilder.withCarrierId(2L).withCarrierName("name 2").withCarrierPhoneNumber("456").build();
        CarrierDTO carrierDTO2 = carrierMapper.carrierToCarrierDTO(carrier2);

        List<CarrierDTO> expectedList = List.of(carrierDTO1, carrierDTO2);

        when(carrierService.findAll()).thenReturn(expectedList);

        mockMvc.perform(get("/api/carriers"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", is(String.valueOf(expectedList.size()))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
    }

    @Test
    public void testCreate() throws Exception {
        CarrierCreateDTO carrierCreateDTO = CarrierFactory.createCarrierCreateDTO();
        CarrierDTO carrierDTO = carrierMapper.carrierCreateDTOToCarrierDTO(carrierCreateDTO);
        carrierDTO.setId(carrierId);

        when(carrierService.saveCarrier(carrierCreateDTO)).thenReturn(carrierDTO);

        mockMvc.perform(post("/api/carriers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carrierCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(carrierDTO)));
    }

    @Test
    public void testUpdate() throws Exception {
        CarrierUpdateDTO carrierUpdateDTO = CarrierFactory.createCarrierUpdateDTO();
        CarrierDTO carrierDTO = carrierMapper.carrierUpdateDTOToCarrierDTO(carrierUpdateDTO);
        carrierDTO.setId(carrierId);

        when(carrierService.updateCarrier(carrierUpdateDTO, carrierId)).thenReturn(carrierDTO);

        mockMvc.perform(put("/api/carriers/{id}", carrierId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carrierUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(carrierDTO)));
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(carrierService).deleteCarrier(carrierId);

        mockMvc.perform(delete("/api/carriers/{id}", carrierId))
                .andExpect(status().isNoContent());
    }
}