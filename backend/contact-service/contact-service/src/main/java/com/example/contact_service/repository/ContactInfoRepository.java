package com.example.contact_service.repository;

import com.example.contact_service.model.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactInfoRepository extends JpaRepository<ContactInfo, Long> { // 2. Folosim ContactInfo și Long
    // Corpul rămâne gol. Magia Spring Data JPA funcționează la fel.
}