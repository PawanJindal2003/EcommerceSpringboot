package com.Project.ecommerce.repositories.product;

import com.Project.ecommerce.entities.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByCategoryId(String id);
    Product getNameByBrandAndSellerIdAndCategoryId(String brandName, String sellerId, String categoryId);
}
