package com.Project.ecommerce.entities.category;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetaDataFieldValuesId implements Serializable {
    private String category;
    private String categoryMetaDataField;
}