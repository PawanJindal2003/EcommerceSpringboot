package com.Project.ecommerce.dto.product.seller;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SellerProductVariationDTO {
    private Integer quantityAvailable;
    private Long price;
    private Map<String, String > metaData;
    private String primaryImageName;
    private List<String> secondaryImageNames;
    private Boolean isActive;
    private SellerProductDTO product;
}
