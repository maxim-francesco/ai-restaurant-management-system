package com.example.gallery_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "events")
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String photoUrl; // Aici vom stoca calea sau URL-ul către imagine

    // Definim relația: Mai multe evenimente (Many) aparțin unei singure categorii (One).
    // Aceasta este partea care "deține" relația.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false) // Creează o coloană "category_id" în tabelul "events"
    // care va fi cheie externă către tabelul "event_categories".
    private EventCategory category;
}