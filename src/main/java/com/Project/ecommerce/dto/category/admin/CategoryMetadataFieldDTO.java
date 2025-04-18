package com.Project.ecommerce.dto.category.admin;

import com.Project.ecommerce.entities.category.CategoryMetaDataFieldValues;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CategoryMetadataFieldDTO {
    private String fieldName;
    private String fieldValues;
}