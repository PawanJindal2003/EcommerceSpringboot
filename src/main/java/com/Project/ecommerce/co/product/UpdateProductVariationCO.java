package com.Project.ecommerce.co.product;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UpdateProductVariationCO {
    private Integer quantityAvailable;
    private Long price;
    private Map<String, String> metadata;
    private Boolean isActive;
}
