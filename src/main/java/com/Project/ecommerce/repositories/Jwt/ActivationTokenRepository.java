package com.Project.ecommerce.repositories.Jwt;

import com.Project.ecommerce.entities.jwt.ActivationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, String> {
    Optional<ActivationToken> findByJwtToken(String jwtToken);
    @Transactional
    @Modifying
    void deleteByEmail(String email);
}
