package com.Project.ecommerce.entities.jwt;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
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
    @Column(length = 36)
    private String id;

    @PrePersist
    public void prePersist(){
        if(id == null){
            id = UuidCreator.getTimeOrderedEpoch().toString();
        }
    }

    private String token;

    private String email;

    private Instant createdAt;

    public RefreshToken(String token, String email, Instant createdAt) {
        this.token = token;
        this.email = email;
        this.createdAt = createdAt;
    }
}

