package com.example.userservice.mapper;

import com.example.userservice.dto.*;
import com.example.userservice.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequestDTO dto);
    UserResponseDTO toResponseDTO(User user);
}
