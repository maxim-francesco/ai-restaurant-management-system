package com.example.contact_service.service;

import com.example.contact_service.dto.ContactInfoDTO;
import com.example.contact_service.model.ContactInfo;
import com.example.contact_service.repository.ContactInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // 1. Marchează clasa ca fiind o componentă de tip Service în Spring
@RequiredArgsConstructor // 2. Adnotare Lombok ce generează un constructor cu toate câmpurile 'final'
public class ContactInfoService {

    // 3. Injectăm Repository-ul pentru a putea interacționa cu baza de date
    private final ContactInfoRepository contactInfoRepository;

    /**
     * Metodă pentru a obține informațiile de contact.
     * Presupunem că va exista o singură înregistrare în tabel.
     * Returnează prima înregistrare găsită sau null dacă tabelul este gol.
     */
    @Transactional(readOnly = true) // Specificăm că aceasta este o tranzacție "doar pentru citire" (mai eficient)
    public ContactInfoDTO getContactInfo() {
        return contactInfoRepository.findAll().stream()
                .findFirst() // Luăm primul element din listă
                .map(this::convertToDto) // Îl convertim în DTO dacă există
                .orElse(null); // Returnăm null dacă nu există nicio înregistrare
    }

    /**
     * Metodă pentru a crea sau actualiza informațiile de contact.
     * @param contactInfoDTO Datele primite de la client.
     * @return Datele salvate, convertite înapoi în DTO.
     */
    @Transactional // Tranzacție standard (citire și scriere)
    public ContactInfoDTO createOrUpdateContactInfo(ContactInfoDTO contactInfoDTO) {
        // Căutăm dacă există deja o înregistrare pentru a o actualiza
        // Dacă nu, vom crea una nouă
        ContactInfo contactInfo = contactInfoRepository.findAll().stream()
                .findFirst()
                .orElse(new ContactInfo()); // Dacă nu găsim nimic, creăm o instanță nouă

        // Transferăm datele din DTO în Entitate
        contactInfo.setAddress(contactInfoDTO.getAddress());
        contactInfo.setPhone(contactInfoDTO.getPhone());
        contactInfo.setEmail(contactInfoDTO.getEmail());
        contactInfo.setSchedule(contactInfoDTO.getSchedule());
        contactInfo.setFacebookUrl(contactInfoDTO.getFacebookUrl());
        contactInfo.setInstagramUrl(contactInfoDTO.getInstagramUrl());
        contactInfo.setTripadvisorUrl(contactInfoDTO.getTripadvisorUrl());

        // Salvăm entitatea în baza de date. 'save' funcționează și pentru creare și pentru actualizare.
        ContactInfo savedContactInfo = contactInfoRepository.save(contactInfo);

        // Convertim entitatea salvată înapoi în DTO și o returnăm
        return convertToDto(savedContactInfo);
    }


    // ====== Metode Private de Conversie (Mappers) ======

    /**
     * Convertește un obiect de tip Entitate (ContactInfo) într-un DTO (ContactInfoDTO).
     */
    private ContactInfoDTO convertToDto(ContactInfo contactInfo) {
        ContactInfoDTO dto = new ContactInfoDTO();
        dto.setId(contactInfo.getId());
        dto.setAddress(contactInfo.getAddress());
        dto.setPhone(contactInfo.getPhone());
        dto.setEmail(contactInfo.getEmail());
        dto.setSchedule(contactInfo.getSchedule());
        dto.setFacebookUrl(contactInfo.getFacebookUrl());
        dto.setInstagramUrl(contactInfo.getInstagramUrl());
        dto.setTripadvisorUrl(contactInfo.getTripadvisorUrl());
        return dto;
    }
}