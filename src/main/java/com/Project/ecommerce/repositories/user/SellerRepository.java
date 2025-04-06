package com.Project.ecommerce.repositories.user;

import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SellerRepository extends JpaRepository<Seller, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCompanyName(String CompanyName);
}
