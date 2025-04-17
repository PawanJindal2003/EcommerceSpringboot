package com.Project.ecommerce.dto.category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubCategoryResponseDTO {
    String id;
    String name;
    String parentId;
}