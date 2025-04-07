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
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ForgetPasswordToken{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;

    private String token;

    private Instant createdAt;

    public ForgetPasswordToken(String token, String email, Instant createdAt) {
        this.email = email;
        this.token = token;
        this.createdAt = createdAt;
    }
}