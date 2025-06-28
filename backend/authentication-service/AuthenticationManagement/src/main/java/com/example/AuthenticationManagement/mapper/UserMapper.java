package com.example.AuthenticationManagement.mapper;

import com.example.AuthenticationManagement.dto.UserRequestDTO;
import com.example.AuthenticationManagement.dto.UserResponseDTO;
import com.example.AuthenticationManagement.model.User;

public class UserMapper {

    // Din DTO de request -> în entitate
    public static User toEntity(UserRequestDTO dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(dto.getPassword()) // ✅ mapare corectă
                .role(dto.getRole())
                .profileImageUrl(dto.getProfileImageUrl())
                .build();
    }


    // Din entitate -> în DTO de răspuns
    public static UserResponseDTO toDto(User user) {
        if (user == null) return null;

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    // Update doar câmpuri specifice din DTO (opțional)
    public static void updateEntity(UserRequestDTO dto, User user) {
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getRole() != null) user.setRole(dto.getRole());
        if (dto.getProfileImageUrl() != null) user.setProfileImageUrl(dto.getProfileImageUrl());
    }
}
