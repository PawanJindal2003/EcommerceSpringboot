package com.Project.ecommerce.repositories.product;

import com.Project.ecommerce.entities.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByCategoryId(String id);

    List<Product> findByBrandAndSellerIdAndCategoryId(String brandName, String sellerId, String categoryId);

    //    Page<Product> findAllBySellerIdAndIsNonDeleted(Specification specification, String sellerId, Pageable pageable);
    Page<Product> findAll(Specification<Product> specification, Pageable pageable);
}
