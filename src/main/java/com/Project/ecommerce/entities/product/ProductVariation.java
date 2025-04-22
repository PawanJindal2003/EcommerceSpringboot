package com.Project.ecommerce.entities.product;

import com.Project.ecommerce.audit.Auditable;
import com.Project.ecommerce.entities.cart.Cart;
import com.Project.ecommerce.entities.order.OrderProduct;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariation extends Auditable {
    @Id
    @Column(length = 36)
    private String id;

    @PrePersist
    public void prePersist(){
        if(id == null){
            id = UuidCreator.getTimeOrderedEpoch().toString();
        }
    }

    private Integer quantityAvailable;
    private Long price;
    private String metaData;
    private String primaryImageName;
    private Boolean isActive = true;

    @ManyToOne
    Product product;

    @OneToMany(mappedBy = "productVariation")
    private List<Cart> carts;

    @OneToMany(mappedBy = "productVariation")
    private List<OrderProduct> orderProducts;
}
