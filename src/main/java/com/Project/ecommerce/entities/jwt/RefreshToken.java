package com.Project.ecommerce.entities.jwt;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String token;

    private String email;

    private Instant createdAt;

    public RefreshToken(String token, String email, Instant createdAt) {
        this.token = token;
        this.email = email;
        this.createdAt = createdAt;
    }
}

