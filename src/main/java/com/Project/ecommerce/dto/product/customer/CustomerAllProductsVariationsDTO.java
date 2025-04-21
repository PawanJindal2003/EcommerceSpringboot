package com.Project.ecommerce.dto.product.customer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CustomerAllProductsVariationsDTO {
    private String productVariationId;
    private Long price;
    private String primaryImage;
}
