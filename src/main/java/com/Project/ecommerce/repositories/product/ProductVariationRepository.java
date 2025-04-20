package com.Project.ecommerce.repositories.product;

import com.Project.ecommerce.entities.product.ProductVariation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, String>, JpaSpecificationExecutor<ProductVariation> {
    Page<ProductVariation> findAll(Pageable pageable);
    Page<ProductVariation> findAll(Specification<ProductVariation> specification, Pageable pageable);
}
