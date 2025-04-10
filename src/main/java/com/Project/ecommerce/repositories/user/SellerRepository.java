package com.Project.ecommerce.repositories.user;

import com.Project.ecommerce.entities.user.Seller;
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
public interface SellerRepository extends JpaRepository<Seller, UUID> {
    @Query("select s from Seller s WHERE (:email is null or lower(s.email) like lower(concat('%', :email, '%')))")
    Page<Seller> findAllByEmail(@Param("email") String email, Pageable pageable);
    Optional<Seller> findByGST(String GST);
    Optional<Seller> findByEmail(String email);
    Optional<Seller> findByCompanyName(String CompanyName);
}
