package com.Project.ecommerce.repositories.product;

import com.Project.ecommerce.entities.product.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, String> {
}
