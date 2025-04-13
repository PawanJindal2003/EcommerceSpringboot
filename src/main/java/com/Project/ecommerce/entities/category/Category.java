package com.Project.ecommerce.entities.category;

import com.Project.ecommerce.entities.product.Product;
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
public class Category {
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

    // self referencing
    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "category")
    private List<CategoryMetaDataFieldValues> metadataFieldValues;

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}