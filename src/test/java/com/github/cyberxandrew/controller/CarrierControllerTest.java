package com.github.cyberxandrew.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cyberxandrew.config.JacksonConfig;
import com.github.cyberxandrew.config.SecurityConfig;
import com.github.cyberxandrew.dto.carrier.CarrierCreateDTO;
import com.github.cyberxandrew.dto.carrier.CarrierDTO;
import com.github.cyberxandrew.dto.carrier.CarrierUpdateDTO;
import com.github.cyberxandrew.mapper.CarrierMapper;
import com.github.cyberxandrew.mapper.CarrierMapperImpl;
import com.github.cyberxandrew.mapper.JsonNullableMapperImpl;
import com.github.cyberxandrew.model.Carrier;
import com.github.cyberxandrew.model.UserDetailsImpl;
import com.github.cyberxandrew.repository.UserRepository;
import com.github.cyberxandrew.repository.UserRepositoryImpl;
import com.github.cyberxandrew.security.JwtRequestFilter;
import com.github.cyberxandrew.security.JwtTokenUtil;
import com.github.cyberxandrew.service.CarrierServiceImpl;
import com.github.cyberxandrew.service.UserDetailsServiceImpl;
import com.github.cyberxandrew.utils.CarrierFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CarrierControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CarrierMapper carrierMapper;
    @MockitoBean private CarrierServiceImpl carrierService;
    @Autowired JwtTokenUtil jwtTokenUtil;
    @Autowired UserDetailsServiceImpl userDetailsService;
    private Long carrierId1;
    private Long carrierId2;
    private String carrierName1;
    private String carrierName2;
    private String carrierPhoneNumber1;
    private String carrierPhoneNumber2;
    private String accessToken;

    @BeforeEach
    void SetUp() {
        carrierId1 = 1L;
        carrierId2 = 2L;
        carrierName1 = "J7";
        carrierName2 = "Java Airlines";
        carrierPhoneNumber1 = "123-456-7890";
        carrierPhoneNumber2 = "098-765-4321";
        accessToken = jwtTokenUtil.generateToken(userDetailsService.loadUserByUsername("test"));
    }

    @Test
    public void testShow() throws Exception {
        CarrierDTO carrierDTO = CarrierFactory.createCarrierDTO();
        carrierDTO.setId(carrierId1);

        when(carrierService.findCarrierById(carrierId1)).thenReturn(carrierDTO);

        mockMvc.perform(get("/api/carriers/{id}", carrierId1)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(carrierDTO)));
    }

    @Test
    public void testIndex() throws Exception {
        CarrierFactory.CarrierBuilder carrierBuilder = new CarrierFactory.CarrierBuilder(); //fixme Took out to factory

        Carrier carrier1 = carrierBuilder.withCarrierId(carrierId1)
                .withCarrierName(carrierName1)
                .withCarrierPhoneNumber(carrierPhoneNumber1).build();
        CarrierDTO carrierDTO1 = carrierMapper.carrierToCarrierDTO(carrier1);

        Carrier carrier2 = carrierBuilder.withCarrierId(carrierId2)
                .withCarrierName(carrierName2)
                .withCarrierPhoneNumber(carrierPhoneNumber2).build();
        CarrierDTO carrierDTO2 = carrierMapper.carrierToCarrierDTO(carrier2);

        List<CarrierDTO> expectedList = List.of(carrierDTO1, carrierDTO2);

        when(carrierService.findAll()).thenReturn(expectedList);

        mockMvc.perform(get("/api/carriers")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", is(String.valueOf(expectedList.size()))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
    }

    @Test
    public void testCreate() throws Exception {
        CarrierCreateDTO carrierCreateDTO = CarrierFactory.createCarrierCreateDTO();
        CarrierDTO carrierDTO = carrierMapper.carrierCreateDTOToCarrierDTO(carrierCreateDTO);
        carrierDTO.setId(carrierId1);

        when(carrierService.saveCarrier(carrierCreateDTO)).thenReturn(carrierDTO);

        mockMvc.perform(post("/api/carriers")
                        .header("Authorization", "Bearer " + accessToken)
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
        carrierDTO.setId(carrierId1);

        when(carrierService.updateCarrier(carrierUpdateDTO, carrierId1)).thenReturn(carrierDTO);

        mockMvc.perform(put("/api/carriers/{id}", carrierId1)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carrierUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(carrierDTO)));
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(carrierService).deleteCarrier(carrierId1);

        mockMvc.perform(delete("/api/carriers/{id}", carrierId1)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());
    }
}