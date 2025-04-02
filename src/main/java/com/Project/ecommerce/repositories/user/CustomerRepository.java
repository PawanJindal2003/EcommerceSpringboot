package com.Project.ecommerce.repositories.user;

import com.Project.ecommerce.entities.user.Customer;
import com.Project.ecommerce.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<User> findByEmail(String email);
}
