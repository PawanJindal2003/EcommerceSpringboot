package com.Project.ecommerce.repositories.user;

import com.Project.ecommerce.entities.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    @Query(value = "SELECT * FROM address WHERE user_id = :userId AND is_deleted = false LIMIT 1", nativeQuery = true)
    Optional<Address> findByUserId(@Param("userId") String userId);
}