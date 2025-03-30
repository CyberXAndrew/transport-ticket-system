package com.github.cyberxandrew.mapper;

import com.github.cyberxandrew.dto.user.UserCreateDTO;
import com.github.cyberxandrew.dto.user.UserDTO;
import com.github.cyberxandrew.dto.user.UserUpdateDTO;
import com.github.cyberxandrew.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = { JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDTO userToUserDTO(User user);
    User userCreateDTOToUser(UserCreateDTO createDTO);
    UserDTO userCreateDTOToUserDTO(UserCreateDTO createDTO);
    UserDTO userUpdateDTOToUserDTO(UserUpdateDTO updateDTO);
    UserUpdateDTO userDTOToUpdateDTO(UserDTO userDTO);
    @Mapping(target = "id", ignore = true)
    void update(UserUpdateDTO updateDTO, @MappingTarget User user);
}