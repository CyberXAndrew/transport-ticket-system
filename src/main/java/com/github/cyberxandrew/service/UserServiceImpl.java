package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.user.UserCreateDTO;
import com.github.cyberxandrew.dto.user.UserDTO;
import com.github.cyberxandrew.dto.user.UserUpdateDTO;
import com.github.cyberxandrew.exception.user.UserNotFoundException;
import com.github.cyberxandrew.exception.user.UserSaveException;
import com.github.cyberxandrew.exception.user.UserUpdateException;
import com.github.cyberxandrew.mapper.UserMapper;
import com.github.cyberxandrew.model.User;
import com.github.cyberxandrew.repository.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired private UserMapper userMapper;
    @Autowired private UserRepositoryImpl userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDTO findUserById(Long userId) {
        return userMapper.userToUserDTO(userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with id" + userId + " not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(user -> userMapper.userToUserDTO(user))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO saveUser(UserCreateDTO createDTO) {
        if (createDTO == null) throw new UserSaveException("Error while saving User: User to save is null");
        User user = userMapper.userCreateDTOToUser(createDTO);
        return userMapper.userToUserDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDTO updateUser(UserUpdateDTO updateDTO, Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserUpdateException("Error while updating User: User by id is null"));

        userMapper.update(updateDTO, user);
        return userMapper.userToUserDTO(userRepository.update(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
