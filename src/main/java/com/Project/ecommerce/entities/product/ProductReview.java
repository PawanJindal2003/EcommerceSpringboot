package com.Project.ecommerce.entities.product;

import com.Project.ecommerce.entities.user.Customer;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview {
    @Id
    @Column(length = 36)
    private String id;

    @PrePersist
    public void prePersist(){
        if(id == null){
            id = UuidCreator.getTimeOrderedEpoch().toString();
        }
    }

    private String review;
    private Integer rating;

    @ManyToOne
    private Customer customer;
    @ManyToOne
    private Product product;
}
