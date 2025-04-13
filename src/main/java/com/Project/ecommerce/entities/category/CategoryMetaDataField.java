package com.Project.ecommerce.entities.category;

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
public class CategoryMetaDataField {
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

    @OneToMany(mappedBy = "categoryMetaDataField")
    private List<CategoryMetaDataFieldValues> fieldValues;
}
