package com.github.cyberxandrew.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cyberxandrew.dto.ticket.TicketCreateDTO;
import com.github.cyberxandrew.dto.ticket.TicketDTO;
import com.github.cyberxandrew.dto.ticket.TicketUpdateDTO;
import com.github.cyberxandrew.dto.ticket.TicketWithRouteDataDTO;
import com.github.cyberxandrew.mapper.TicketMapper;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.security.JwtTokenUtil;
import com.github.cyberxandrew.service.TicketServiceImpl;
import com.github.cyberxandrew.service.UserDetailsServiceImpl;
import com.github.cyberxandrew.utils.TicketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class TicketControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TicketMapper ticketMapper;
    @Autowired private JwtTokenUtil jwtTokenUtil;
    @Autowired private UserDetailsServiceImpl userDetailsService;
    @MockitoBean private TicketServiceImpl ticketService;
    private final String URL = "/api/tickets";
    private String authenticationHeader;
    private Long ticketId1;
    private Long ticketId2;
    private Long userId;

    @BeforeEach
    void setUp() {
        ticketId1 = 1L;
        ticketId2 = 2L;
        userId = 1L;

        String accessToken = jwtTokenUtil.generateToken(userDetailsService.loadUserByUsername("test"));
        authenticationHeader = "Bearer " + accessToken;
    }

    @Test
    public void testShow() throws Exception {
        TicketDTO ticketDTO = TicketFactory.createTicketDTO();
        ticketDTO.setId(ticketId1);

        when(ticketService.findTicketById(ticketId1)).thenReturn(ticketDTO);

        mockMvc.perform(get(URL + "/{id}", ticketId1)
                        .header("Authorization", authenticationHeader))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(ticketDTO)));
    }
    
    @Test
    public void testFindAllPurchasedTickets() throws Exception {
        TicketFactory.TicketBuilder ticketBuilder = new TicketFactory.TicketBuilder(); // Fix get out to factory?
        Ticket ticket1 = ticketBuilder.withId(ticketId1).withUserId(userId).build();
        TicketDTO ticketDTO1 = ticketMapper.ticketToTicketDTO(ticket1);
        Ticket ticket2 = ticketBuilder.withId(ticketId2).withUserId(userId).build();
        TicketDTO ticketDTO2 = ticketMapper.ticketToTicketDTO(ticket2);
        List<TicketDTO> tickets = new ArrayList<>(List.of(ticketDTO1, ticketDTO2));

        when(ticketService.findAllPurchasedTickets(userId)).thenReturn(tickets);

        mockMvc.perform(get(URL + "/purchased?userId=" + userId)
                        .header("Authorization", authenticationHeader))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(tickets)));
    }

    @Test
    public void testIndexWithoutFilters() throws Exception {
        List<TicketWithRouteDataDTO> expectedList = getTicketWithRouteDataDTOS();

        when(ticketService.findAllAccessibleTickets(null, null, null,
                null, null)).thenReturn(expectedList);

        mockMvc.perform(get(URL)
                        .header("Authorization", authenticationHeader))
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

        when(ticketService.findAllAccessibleTickets(pageRequest, dateTime, "Saint Petersburg",
                "Moscow", "J7")).thenReturn(expectedList);

        mockMvc.perform(get(URL + "?page=0&size=2" +
                "&dateTime=2025-01-02T23:59:59.123456789" +
                "&departurePoint=Saint Petersburg&destinationPoint=Moscow&carrierName=J7")
                        .header("Authorization", authenticationHeader))
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
        ticketDTO.setId(ticketId1);

        when(ticketService.saveTicket(ticketCreateDTO)).thenReturn(ticketDTO);

        mockMvc.perform(post(URL)
                        .header("Authorization", authenticationHeader)
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
        ticketDTO.setId(ticketId1);

        when(ticketService.updateTicket(ticketUpdateDTO, ticketId1)).thenReturn(ticketDTO);

        mockMvc.perform(put(URL + "/{id}", ticketId1)
                        .header("Authorization", authenticationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(ticketDTO)));
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(ticketService).deleteTicket(ticketId1);

        mockMvc.perform(delete(URL + "/{id}", ticketId1)
                        .header("Authorization", authenticationHeader))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testPurchaseTicket() throws Exception {
        doNothing().when(ticketService).purchaseTicket(userId, ticketId1);

        mockMvc.perform(post(URL + "/{id}/purchase?userId=2", ticketId1)
                        .header("Authorization", authenticationHeader))
                .andExpect(status().isOk());
    }
}