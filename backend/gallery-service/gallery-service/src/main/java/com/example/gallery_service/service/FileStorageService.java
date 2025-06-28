package com.example.gallery_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    // Metoda va salva fișierul și va returna numele unic generat pentru el
    String storeFile(MultipartFile file);

    // Metoda va șterge fișierul de pe disc pe baza numelui său
    void deleteFile(String fileName);
}