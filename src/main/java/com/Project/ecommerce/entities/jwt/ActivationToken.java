package com.Project.ecommerce.entities.jwt;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivationToken {
    @Id
    @Column(length = 36)
    private String id;

    @PrePersist
    public void prePersist(){
        if(id == null){
            id = UuidCreator.getTimeOrderedEpoch().toString();
        }
    }

    @Email
    String email;

    String jwtToken;

    private Instant createdAt;

    public ActivationToken(String jwtToken, String email, Instant createdAt) {
        this.email = email;
        this.jwtToken = jwtToken;
        this.createdAt = createdAt;
    }
}
