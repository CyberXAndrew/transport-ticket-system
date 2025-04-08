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

    @Mapping(target = "role", expression = "java(createDTO.getRole().name())")
    User userCreateDTOToUser(UserCreateDTO createDTO);

    UserDTO userCreateDTOToUserDTO(UserCreateDTO createDTO);
    UserDTO userUpdateDTOToUserDTO(UserUpdateDTO updateDTO);

    @Mapping(target = "id", ignore = true) //Fix заставляем выполнить код когда нет поля role
    @Mapping(target = "role", expression = "java(updateDTO.getRole() != null ? (updateDTO.getRole().get() != null ?" +
            " updateDTO.getRole().get().name() : null) : user.getRole())")
    void update(UserUpdateDTO updateDTO, @MappingTarget User user);
}