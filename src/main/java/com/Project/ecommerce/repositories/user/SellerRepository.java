package com.Project.ecommerce.repositories.user;

import com.Project.ecommerce.entities.user.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SellerRepository extends JpaRepository<Seller, UUID> {
}
