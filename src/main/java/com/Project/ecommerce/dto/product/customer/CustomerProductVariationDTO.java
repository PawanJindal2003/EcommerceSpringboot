package com.Project.ecommerce.dto.product.customer;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CustomerProductVariationDTO {
    private Map<String, String> metadata;
    private Long price;
    private String primaryImage;
    private List<String> secondaryImages;
}
