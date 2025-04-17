package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.user.UserCreateDTO;
import com.github.cyberxandrew.dto.user.UserDTO;
import com.github.cyberxandrew.dto.user.UserUpdateDTO;
import com.github.cyberxandrew.exception.user.UserHasTicketsException;
import com.github.cyberxandrew.exception.user.UserNotFoundException;
import com.github.cyberxandrew.exception.user.UserSaveException;
import com.github.cyberxandrew.model.Role;
import com.github.cyberxandrew.utils.UserFactory;
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
@ActiveProfiles("test")
@Transactional
public class UserServiceImplIntegrationTest {
    @Autowired private UserServiceImpl userService;
    private Long absentId;
    private Long idOfSavedUser;
    private String login;
    private String password;
    private String fullName;

    @BeforeEach
    public void setUp() {
        absentId = 999L;
        idOfSavedUser = 1L;
        login = "test login";
        password = "test password";
        fullName = "test fullname";
    }

    @Test
    public void findUserById() {
        UserCreateDTO createDTO = UserFactory.createUserCreateDTO();
        UserDTO savedUserDto = userService.saveUser(createDTO);

        UserDTO userDto = userService.findUserById(savedUserDto.getId());

        assertEquals(userDto, savedUserDto);
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(absentId));
    }

    @Test
    public void findAll() {
        List<UserDTO> allUsers = userService.findAll();

        assertFalse(allUsers.isEmpty());
    }

    @Test
    public void saveUser() {
        UserCreateDTO createDTO = UserFactory.createUserCreateDTO();

        UserDTO savedUser = userService.saveUser(createDTO);

        assertTrue(savedUser != null && savedUser.getId() > 0);
        assertThrows(UserSaveException.class, () -> userService.saveUser(null));
    }

    @Test
    public void updateUser() {
        UserDTO userDTOFromDb = userService.findUserById(idOfSavedUser);

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setLogin(JsonNullable.of(login));
        updateDTO.setPassword(JsonNullable.of(password));
        updateDTO.setFullName(JsonNullable.of(fullName));

        userService.updateUser(updateDTO, idOfSavedUser);
        UserDTO updatedUserDto = userService.findUserById(idOfSavedUser);

        assertEquals(updatedUserDto.getId(), userDTOFromDb.getId());
        assertNotEquals(updatedUserDto.getLogin(), userDTOFromDb.getLogin());
        assertNotEquals(updatedUserDto.getPassword(), userDTOFromDb.getPassword());
        assertNotEquals(updatedUserDto.getFullName(), userDTOFromDb.getFullName());
    }

    @Test
    public void deleteBoundedWithTicketsUser() {
        assertThrows(UserHasTicketsException.class, () -> userService.deleteUser(idOfSavedUser));
    }

    @Test
    public void deleteUser() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setLogin(login);
        createDTO.setPassword(password);
        createDTO.setFullName(fullName);
        createDTO.setRole(Role.CUSTOMER);

        UserDTO savedUserDTO = userService.saveUser(createDTO);

        assertEquals(savedUserDTO.getLogin(), createDTO.getLogin());

        userService.deleteUser(savedUserDTO.getId());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(savedUserDTO.getId()));
    }
}
