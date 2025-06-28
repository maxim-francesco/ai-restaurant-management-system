package com.example.gallery_service.model;

import jakarta.persistence.*;
import lombok.Data; // Importăm adnotarea @Data din Lombok

import java.util.List;

@Entity // Marchează această clasă ca fiind o entitate JPA (un tabel în BD)
@Table(name = "event_categories") // Specifică numele tabelului în baza de date
@Data // Adnotare Lombok: generează automat getters, setters, toString(), equals(), hashCode()
public class EventCategory {

    @Id // Marchează acest câmp ca fiind cheia primară a tabelului
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configurează generarea automată a ID-ului (auto-increment în MySQL)
    private Long id;

    @Column(nullable = false, unique = true) // Specifică faptul că coloana "name" nu poate fi goală și trebuie să fie unică
    private String name;

    // Definim relația: O categorie (One) are mai multe evenimente (Many)
    // "mappedBy" indică faptul că entitatea "Event" este cea care "deține" relația (prin câmpul "category").
    // "cascade = CascadeType.ALL" înseamnă că orice operație (salvare, ștergere) aplicată unei categorii
    // se va propaga și la evenimentele asociate.
    // "fetch = FetchType.LAZY" înseamnă că evenimentele nu sunt încărcate din BD decât atunci când avem nevoie explicit de ele.
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Event> events;
}