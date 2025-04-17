package com.Project.ecommerce.co.category.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MetadataValueCategoryCO {
    private String categoryId;
    private String metaDataFieldId;

    @Size(min = 1, message = "There should at least be a single value to add")
    private List<String> values;
}
