package com.Project.ecommerce.entities.user;

import com.Project.ecommerce.entities.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seller extends User{
    private String GST;
    private String companyContact;
    private String companyName;

    @OneToMany(mappedBy = "seller")
    private List<Product> products;
}
