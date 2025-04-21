package com.Project.ecommerce.dto.product.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerProductCategoryDTO {
    String categoryId;
    String categoryName;
    String categoryParentId;
}
