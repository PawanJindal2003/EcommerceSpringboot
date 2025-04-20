package com.Project.ecommerce.co.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductCO {
    private String name;
    private String description;
    private Boolean isCancellable;
    private Boolean isReturnable;
}
