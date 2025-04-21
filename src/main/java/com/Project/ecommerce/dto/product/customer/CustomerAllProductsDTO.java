package com.Project.ecommerce.dto.product.customer;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
public class CustomerAllProductsDTO {
    private String id;
    private String name;
    private String brand;
    private String retailer;
    private List<CustomerAllProductsVariationsDTO> productVariations;
    private CustomerProductCategoryDTO categories;
}
