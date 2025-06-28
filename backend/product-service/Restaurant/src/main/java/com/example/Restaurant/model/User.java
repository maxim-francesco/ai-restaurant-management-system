package com.example.Restaurant.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String profileImageUrl;

    private String password;


    // --- NOU: Implementarea metodelor din UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Returnăm rolul utilizatorului într-un format înțeles de Spring Security
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        // Pentru Spring Security, "username"-ul este identificatorul unic. În cazul nostru, acesta este email-ul.
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Pentru o implementare simplă, returnăm true.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}