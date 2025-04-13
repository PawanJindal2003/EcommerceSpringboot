package com.Project.ecommerce.entities.cart;

import com.Project.ecommerce.entities.product.ProductVariation;
import com.Project.ecommerce.entities.user.Customer;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Integer quantity;
    private Boolean isWishlistItem;

    @Id
    @ManyToOne
    private Customer customer;

    @Id
    @ManyToOne
    private ProductVariation productVariation;
}