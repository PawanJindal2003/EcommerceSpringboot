package com.Project.ecommerce.entities.category;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetaDataFieldValues {
    @EmbeddedId
    private CategoryMetaDataFieldValuesId id;

    private String value;

    //composite key
//    @Id
    @MapsId("category")
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    //composite key
//    @Id
    @MapsId("categoryMetaDataField")
    @ManyToOne
    @JoinColumn(name = "category_metadata_field_id")
    private CategoryMetaDataField categoryMetaDataField;
}