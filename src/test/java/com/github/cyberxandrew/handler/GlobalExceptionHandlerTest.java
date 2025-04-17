package com.github.cyberxandrew.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cyberxandrew.dto.ticket.TicketCreateDTO;
import com.github.cyberxandrew.security.JwtTokenUtil;
import com.github.cyberxandrew.service.TicketServiceImpl;
import com.github.cyberxandrew.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GlobalExceptionHandlerTest {
    @Autowired MockMvc mockMvc;
    @Autowired Validator validator;
    @Autowired ObjectMapper objectMapper;
    @Autowired private JwtTokenUtil jwtTokenUtil;
    @Autowired private UserDetailsServiceImpl userDetailsService;
    @MockitoBean TicketServiceImpl ticketService;
    private String accessToken;

    @BeforeEach
    void beforeEach() {
        accessToken = jwtTokenUtil.generateToken(userDetailsService.loadUserByUsername("test"));
    }

    @Test
    void handleMethodArgumentNotValidException() throws Exception {
        TicketCreateDTO violatedCreateDTO = new TicketCreateDTO();
        violatedCreateDTO.setDateTime(null);
        violatedCreateDTO.setRouteId(null);
        violatedCreateDTO.setPrice(null);
        violatedCreateDTO.setSeatNumber(null);

        Errors errors = new BeanPropertyBindingResult(violatedCreateDTO, "violatedCreateDTO");
        validator.validate(violatedCreateDTO, errors);

        mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(violatedCreateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"status\":400")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"error\":\"Bad Request\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"message\":\"Validation failed\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("routeId: must not be null")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("dateTime: must not be null")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("seatNumber: must not be blank")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("price: must not be null")));
    }
}


