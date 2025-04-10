package com.Project.ecommerce.repositories.user;

import com.Project.ecommerce.entities.user.Customer;
import com.Project.ecommerce.entities.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    @Query("select c from Customer c WHERE (:email is null or lower(c.email) like lower(concat('%', :email, '%')))")
    Page<Customer> findAllByEmail(@Param("email") String email, Pageable pageable);
    Optional<Customer> findByEmail(String email);
}