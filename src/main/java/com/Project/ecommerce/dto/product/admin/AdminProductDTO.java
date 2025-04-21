package com.Project.ecommerce.dto.product.admin;

import com.Project.ecommerce.dto.product.customer.CustomerAllProductsVariationsDTO;
import com.Project.ecommerce.dto.product.customer.CustomerProductCategoryDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminProductDTO {
    private String name;
    private String description;
    private Boolean isCancellable;
    private Boolean isReturnable;
    private String brand;
    private Boolean isActive;
    private Boolean isDeleted;
    private CustomerProductCategoryDTO category;
    private List<AdminProductVariationDTO> variations;
    private SellerDetailsDTO sellerDetails;
}
