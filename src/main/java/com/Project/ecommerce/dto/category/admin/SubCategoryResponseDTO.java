package com.Project.ecommerce.dto.category.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubCategoryResponseDTO {
    private String id;
    private String name;
    private String parentId;
}