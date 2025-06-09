package com.example.userservice.service;


import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        User user = userMapper.toEntity(requestDTO);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toResponseDTO);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponseDTO).collect(Collectors.toList());
    }

    public Optional<UserResponseDTO> updateUser(Long id, UserRequestDTO requestDTO) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setName(requestDTO.getName());
            existingUser.setEmail(requestDTO.getEmail());
            existingUser.setAge(requestDTO.getAge());

            User updated = userRepository.save(existingUser);
            return userMapper.toResponseDTO(updated);
        });
    }

    public boolean deleteUser(Long id) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

