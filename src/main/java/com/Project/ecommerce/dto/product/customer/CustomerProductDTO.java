package com.Project.ecommerce.dto.product.customer;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CustomerProductDTO {
    private String name;
    private String description;
    private Boolean isCancellable;
    private Boolean isReturnable;
    private String brand;
    private String category;
    private List<CustomerProductVariationDTO> productVariation;
}
