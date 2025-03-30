package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.user.UserCreateDTO;
import com.github.cyberxandrew.dto.user.UserDTO;
import com.github.cyberxandrew.dto.user.UserUpdateDTO;

import java.util.List;

public interface UserService {
    UserDTO findUserById(Long userId);
    List<UserDTO> findAll();
    UserDTO saveUser(UserCreateDTO createDTO);
    UserDTO updateUser(UserUpdateDTO updateDTO, Long id);
    void deleteUser(Long userId);
}
