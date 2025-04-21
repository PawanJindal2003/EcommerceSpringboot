package com.Project.ecommerce.dto.product.customer;

import com.Project.ecommerce.dto.category.customer.CustomerCategoryResponseDTO;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CustomerProductDTO {
    private String name;
    private String brand;
    private String description;
    private Boolean isCancellable;
    private Boolean isReturnable;
    private List<CustomerProductCategoryDTO> category;
    private List<CustomerProductVariationDTO> productVariation;
}
