package com.Project.ecommerce.entities.product;

import com.Project.ecommerce.entities.category.Category;
import com.Project.ecommerce.entities.user.Seller;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @Column(length = 36)
    private String id;

    @PrePersist
    public void prePersist(){
        if(id == null){
            id = UuidCreator.getTimeOrderedEpoch().toString();
        }
    }

    private String name;
    private String description;
    private Boolean isCancellable = false;
    private Boolean isReturnable = false;
    private String brand;
    private Boolean isActive = false;
    private Boolean isDeleted = false;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Seller seller;

    @OneToMany(mappedBy = "product")
    private List<ProductVariation> productVariations;

    @OneToMany(mappedBy = "product")
    private List<ProductReview> productReviews;
}
