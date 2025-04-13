package com.Project.ecommerce.entities.user;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @Column(length = 36)
    private String id;

    @PrePersist
    public void prePersist(){
        if(id == null){
            id = UuidCreator.getTimeOrderedEpoch().toString();
        }
    }

    private String authority;

    public Role(String authority){
        this.authority = authority;
    }

    @OneToMany(mappedBy = "role")
    private Set<User> users;
}