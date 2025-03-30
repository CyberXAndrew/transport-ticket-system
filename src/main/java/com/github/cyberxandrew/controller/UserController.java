package com.github.cyberxandrew.controller;

import com.github.cyberxandrew.dto.user.UserCreateDTO;
import com.github.cyberxandrew.dto.user.UserDTO;
import com.github.cyberxandrew.dto.user.UserUpdateDTO;
import com.github.cyberxandrew.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @GetMapping(path = "")
    public ResponseEntity<List<UserDTO>> index() {
        List<UserDTO> allUsers = userService.findAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(allUsers.size()))
                .body(allUsers);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO createDTO) {
        return userService.saveUser(createDTO);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@Valid @RequestBody UserUpdateDTO updateDTO, @PathVariable Long id) {
        return userService.updateUser(updateDTO, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }
    
}
