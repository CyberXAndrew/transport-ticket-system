package com.github.cyberxandrew.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cyberxandrew.config.JacksonConfig;
import com.github.cyberxandrew.dto.TicketCreateDTO;
import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.dto.TicketUpdateDTO;
import com.github.cyberxandrew.dto.TicketWithRouteDataDTO;
import com.github.cyberxandrew.mapper.JsonNullableMapperImpl;
import com.github.cyberxandrew.mapper.TicketMapper;
import com.github.cyberxandrew.mapper.TicketMapperImpl;
import com.github.cyberxandrew.service.TicketServiceImpl;
import com.github.cyberxandrew.utils.TicketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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

@WebMvcTest(TicketController.class)
@Import({TicketMapperImpl.class, JsonNullableMapperImpl.class, JacksonConfig.class})
@ActiveProfiles("test")
public class TicketControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TicketMapper ticketMapper;
    @MockitoBean private TicketServiceImpl ticketService;
    private Long testTicketId;

    @BeforeEach
    void setUp() {
        testTicketId = 1L;
    }

    @Test
    void testShow() throws Exception {
        TicketDTO ticketDTO = TicketFactory.createTicketDTO();
        ticketDTO.setId(testTicketId);

        when(ticketService.findTicketById(testTicketId)).thenReturn(ticketDTO);

        mockMvc.perform(get("/api/tickets/{id}", testTicketId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(ticketDTO)));
    }

    @Test
    public void testIndexWithoutFilters() throws Exception {
        List<TicketWithRouteDataDTO> expectedList = getTicketWithRouteDataDTOS();

        when(ticketService.findAllAccessibleTickets(null, null, null,
                null, null)).thenReturn(expectedList);

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", is(String.valueOf(expectedList.size()))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
    }

    @Test
    public void testIndexWithFilters() throws Exception {
        List<TicketWithRouteDataDTO> expectedList = getTicketWithRouteDataDTOS();

        Pageable pageRequest = PageRequest.of(0, 2);
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 2, 23, 59, 59,
                123456789);

        when(ticketService.findAllAccessibleTickets(pageRequest, dateTime, "Saints-Petersburg",
                "Moscow", "J7")).thenReturn(expectedList);

        mockMvc.perform(get("/api/tickets?page=0&size=2" +
                "&dateTime=2025-01-02T23:59:59.123456789" +
                "&departurePoint=Saints-Petersburg&destinationPoint=Moscow&carrierName=J7"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", is(String.valueOf(expectedList.size()))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
    }

    private static List<TicketWithRouteDataDTO> getTicketWithRouteDataDTOS() {
        TicketWithRouteDataDTO ticketWithRouteDataDTO1 = new TicketWithRouteDataDTO();
        TicketFactory.setTicketWithRouteDataDtoFieldsWithoutUserId(ticketWithRouteDataDTO1);
        TicketWithRouteDataDTO ticketWithRouteDataDTO2 = new TicketWithRouteDataDTO();
        TicketFactory.setTicketWithRouteDataDtoFieldsWithoutUserId(ticketWithRouteDataDTO2);
        ticketWithRouteDataDTO2.setRouteId(2L);
        ticketWithRouteDataDTO2.setSeatNumber("2B");

        return List.of(ticketWithRouteDataDTO1, ticketWithRouteDataDTO2);
    }

    @Test
    public void testCreate() throws Exception {
        TicketCreateDTO ticketCreateDTO = TicketFactory.createTicketCreateDTO();
        TicketDTO ticketDTO = ticketMapper.ticketCreateDTOToTicketDTO(ticketCreateDTO);
        ticketDTO.setId(testTicketId);

        when(ticketService.saveTicket(ticketCreateDTO)).thenReturn(ticketDTO);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(ticketDTO)));
    }

    @Test
    public void testUpdate() throws Exception {
        TicketUpdateDTO ticketUpdateDTO = TicketFactory.createTicketUpdateDTO();
        TicketDTO ticketDTO = ticketMapper.ticketUpdateDTOToTicketDTO(ticketUpdateDTO);
        ticketDTO.setId(testTicketId);

        when(ticketService.updateTicket(ticketUpdateDTO, testTicketId)).thenReturn(ticketDTO);

        mockMvc.perform(put("/api/tickets/{id}", testTicketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(ticketDTO)));
    }

    @Test
    public void testDelete() throws Exception {

        doNothing().when(ticketService).deleteTicket(testTicketId);

        mockMvc.perform(delete("/api/tickets/{id}", testTicketId))
                .andExpect(status().isNoContent());
    }
}