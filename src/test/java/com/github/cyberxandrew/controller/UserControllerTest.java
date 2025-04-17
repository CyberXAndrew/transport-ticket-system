package com.github.cyberxandrew.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cyberxandrew.dto.user.UserCreateDTO;
import com.github.cyberxandrew.dto.user.UserDTO;
import com.github.cyberxandrew.dto.user.UserUpdateDTO;
import com.github.cyberxandrew.mapper.UserMapper;
import com.github.cyberxandrew.model.User;
import com.github.cyberxandrew.security.JwtTokenUtil;
import com.github.cyberxandrew.service.UserDetailsServiceImpl;
import com.github.cyberxandrew.service.UserServiceImpl;
import com.github.cyberxandrew.utils.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private JwtTokenUtil jwtTokenUtil;
    @Autowired private UserDetailsServiceImpl userDetailsService;
    @MockitoBean private UserServiceImpl userService;
    private Long userId1;
    private Long userId2;
    private String login1;
    private String login2;
    private String password1;
    private String password2;
    private String fullName1;
    private String fullName2;
    private String accessToken;

    @BeforeEach
    void SetUp() {
        userId1 = 1L;
        userId2 = 2L;
        login1 = "test login 1";
        login2 = "test login 2";
        password1 = "test password 1";
        password2 = "test password 2";
        fullName1 = "test fullname 1";
        fullName2 = "test fullname 2";
        accessToken = jwtTokenUtil.generateToken(userDetailsService.loadUserByUsername("test"));
    }

    @Test
    public void testShow() throws Exception {
        UserDTO userDTO = UserFactory.createUserDTO();
        userDTO.setId(userId1);

        when(userService.findUserById(userId1)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/{id}", userId1)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));
    }

    @Test
    public void testIndex() throws Exception {
        UserFactory.UserBuilder userBuilder = new UserFactory.UserBuilder(); //fixme Took out to factory

        User user1 = userBuilder.withUserId(userId1)
                .withUserLogin(login1)
                .withUserPassword(password1)
                .withUserFullName(fullName1).build();
        UserDTO userDTO1 = userMapper.userToUserDTO(user1);

        User user2 = userBuilder.withUserId(userId2)
                .withUserLogin(login2)
                .withUserPassword(password2)
                .withUserFullName(fullName2).build();
        UserDTO userDTO2 = userMapper.userToUserDTO(user2);

        List<UserDTO> expectedList = List.of(userDTO1, userDTO2);

        when(userService.findAll()).thenReturn(expectedList);

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", is(String.valueOf(expectedList.size()))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
    }

    @Test
    public void testCreate() throws Exception {
        UserCreateDTO userCreateDTO = UserFactory.createUserCreateDTO();
        UserDTO userDTO = userMapper.userCreateDTOToUserDTO(userCreateDTO);
        userDTO.setId(userId1);

        when(userService.saveUser(userCreateDTO)).thenReturn(userDTO);

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));
    }

    @Test
    public void testUpdate() throws Exception {
        UserUpdateDTO userUpdateDTO = UserFactory.createUserUpdateDTO();
        UserDTO userDTO = userMapper.userUpdateDTOToUserDTO(userUpdateDTO);
        userDTO.setId(userId1);

        when(userService.updateUser(userUpdateDTO, userId1)).thenReturn(userDTO);

        mockMvc.perform(put("/api/users/{id}", userId1)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(userService).deleteUser(userId1);

        mockMvc.perform(delete("/api/users/{id}", userId1)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());
    }
}
