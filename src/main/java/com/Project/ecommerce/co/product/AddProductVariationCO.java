package com.Project.ecommerce.co.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class AddProductVariationCO {
    @NotBlank(message = "Product Id cannot be blank")
    private String productId;

    @NotEmpty(message = "Metadata cannot be empty")
    private Map<String , String> metadata;

    @NotNull(message = "Quantity cannot be blank")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantityAvailable;

    @NotNull(message = "Price cannot be blank")
    @Min(value = 0, message = "Price cannot be negative")
    private Long price;
}
