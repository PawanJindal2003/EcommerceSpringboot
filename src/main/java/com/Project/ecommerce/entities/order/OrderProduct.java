package com.Project.ecommerce.entities.order;

import com.Project.ecommerce.entities.product.ProductVariation;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct {
    @Id
    @Column(length = 36)
    private String id;

    @PrePersist
    public void prePersist(){
        if(id == null){
            id = UuidCreator.getTimeOrderedEpoch().toString();
        }
    }

    private Integer quantity;
    private Long price;

    @ManyToOne
    private OrderName orderName;

    @ManyToOne
    private ProductVariation productVariation;

    @OneToMany(mappedBy = "orderProduct")
    private List<OrderStatus> orderStatuses;
}
