package com.Project.ecommerce.dto.product.seller;

import com.Project.ecommerce.dto.category.admin.CategoryResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SellerProductDTO {
    private String id;
    private String name;
    private String description;
    private Boolean isCancellable;
    private Boolean isReturnable;
    private String brand;
    private Boolean isActive;
    private CategoryResponseDTO category;
}
