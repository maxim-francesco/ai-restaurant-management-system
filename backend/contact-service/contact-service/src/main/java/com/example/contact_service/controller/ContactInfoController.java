package com.example.contact_service.controller;

import com.example.contact_service.dto.ContactInfoDTO;
import com.example.contact_service.service.ContactInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // 1. O adnotare specială care combină @Controller cu @ResponseBody
@RequestMapping("/api/contact") // 2. Stabilește un URL de bază pentru toate metodele din această clasă
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://adminapp-cttk.vercel.app", "https://clientapp-648cvri7w-maxim-francescos-projects.vercel.app/"})
public class ContactInfoController {

    private final ContactInfoService contactInfoService;

    /**
     * Endpoint pentru a prelua informațiile de contact.
     * Răspunde la cereri HTTP GET la adresa /api/contact
     */
    @GetMapping
    public ResponseEntity<ContactInfoDTO> getInfo() {
        ContactInfoDTO contactInfo = contactInfoService.getContactInfo();
        if (contactInfo != null) {
            // 3. Dacă găsim informațiile, returnăm un status 200 OK și datele în format JSON
            return ResponseEntity.ok(contactInfo);
        } else {
            // 4. Dacă nu există nicio informație, returnăm un status 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint pentru a crea sau actualiza informațiile de contact.
     * Răspunde la cereri HTTP POST la adresa /api/contact
     * Datele sunt trimise în corpul cererii (request body) în format JSON.
     */
    @PostMapping
    public ResponseEntity<ContactInfoDTO> createOrUpdateInfo(@Valid @RequestBody ContactInfoDTO contactInfoDTO) {
        // 5. @RequestBody spune Spring să convertească JSON-ul din cerere într-un obiect ContactInfoDTO
        ContactInfoDTO savedContactInfo = contactInfoService.createOrUpdateContactInfo(contactInfoDTO);
        return ResponseEntity.ok(savedContactInfo);
    }
}