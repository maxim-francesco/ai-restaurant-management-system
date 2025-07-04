package com.example.gallery_service.service.impl;

import com.example.gallery_service.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service // Marchează clasa ca fiind un serviciu gestionat de Spring
public class FileStorageServiceImpl implements FileStorageService {

    // Definim calea direct în cod, ca o constantă, conform convenției tale.
    // Definim calea direct în cod, ca o constantă, conform convenției tale.
    private final String uploadDir = "/tmp/uploads/gallery";
    private final Path fileStorageLocation;

    // Constructorul folosește constanta definită mai sus.
    public FileStorageServiceImpl() {
        this.fileStorageLocation = Paths.get(this.uploadDir).toAbsolutePath().normalize();
        try {
            // Se asigură că directorul există la pornirea aplicației.
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Nu s-a putut crea directorul pentru stocarea fișierelor.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        try {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        } catch (Exception e) {
            // Ignorăm dacă nu are extensie
        }
        // Generează un nume unic pentru a evita suprascrierea
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Nu s-a putut stoca fișierul " + uniqueFileName, ex);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Nu s-a putut șterge fișierul " + fileName, ex);
        }
    }
}