package com.Project.ecommerce.co.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddProductCO {
    //mandatory
    @NotBlank(message = "Name of the product cannot be blank")
    private String name;
    @NotBlank(message = "Name of the brand cannot be null")
    private String brand;
    @NotBlank(message = "Please mention the category")
    private String categoryId;

    //optional
    private String description;
    private Boolean isCancellable;
    private Boolean isReturnable;
}
