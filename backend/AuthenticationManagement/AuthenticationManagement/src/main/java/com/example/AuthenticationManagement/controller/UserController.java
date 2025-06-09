package com.example.AuthenticationManagement.controller;

import com.example.AuthenticationManagement.dto.LoginRequestDTO;
import com.example.AuthenticationManagement.dto.LoginResponseDTO;
import com.example.AuthenticationManagement.dto.UserRequestDTO;
import com.example.AuthenticationManagement.dto.UserResponseDTO;
import com.example.AuthenticationManagement.model.User;
import com.example.AuthenticationManagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO response = userService.createUser(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
                                                      @RequestBody UserRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.updateUser(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        Optional<User> userOpt = userService.findByEmail(loginRequest.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok(new LoginResponseDTO(
                        "Login successful",
                        user.getId(),
                        user.getRole().name()
                ));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileName = userService.storeProfileImage(id, file);
            return ResponseEntity.ok("Image uploaded successfully: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed");
        }
    }




}
