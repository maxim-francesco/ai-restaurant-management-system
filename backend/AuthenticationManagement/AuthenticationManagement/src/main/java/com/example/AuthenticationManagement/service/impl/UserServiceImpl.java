package com.example.AuthenticationManagement.service.impl;

import com.example.AuthenticationManagement.dto.UserRequestDTO;
import com.example.AuthenticationManagement.dto.UserResponseDTO;
import com.example.AuthenticationManagement.exception.DuplicateFieldException;
import com.example.AuthenticationManagement.mapper.UserMapper;
import com.example.AuthenticationManagement.model.User;
import com.example.AuthenticationManagement.repository.UserRepository;
import com.example.AuthenticationManagement.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateFieldException("Email is already in use");
        }

        if (userRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new DuplicateFieldException("Phone number is already in use");
        }

        User user = UserMapper.toEntity(dto);
        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        return UserMapper.toDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        UserMapper.updateEntity(dto, user);
        User updated = userRepository.save(user);
        return UserMapper.toDto(updated);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public String storeProfileImage(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String uploadDir = "uploads/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        user.setProfileImageUrl("/" + filePath.toString().replace("\\", "/"));
        userRepository.save(user);

        return fileName;
    }


}
