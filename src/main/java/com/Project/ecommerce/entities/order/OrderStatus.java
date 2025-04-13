package com.Project.ecommerce.entities.order;


import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatus {
    @Id
    @Column(length = 36)
    private String id;

    @PrePersist
    public void prePersist(){
        if(id == null){
            id = UuidCreator.getTimeOrderedEpoch().toString();
        }
    }

    private Date transitionDate;
    private String transitionNotesComments;

    @Enumerated(EnumType.STRING)
    private Status fromStatus;

    @Enumerated(EnumType.STRING)
    private Status toStatus;

    @ManyToOne
    private OrderProduct orderProduct;
}
