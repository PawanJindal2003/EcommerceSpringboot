package com.Project.ecommerce.repositories.Jwt;

import com.Project.ecommerce.entities.jwt.BlacklistedAccessToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.time.Instant;

@Repository
public interface BlacklistedAccessTokenRepository extends JpaRepository<BlacklistedAccessToken, String> {
    Boolean existsByToken(String token);

    @Transactional
    @Modifying
    void deleteByExpiryTimeBefore(Instant now);
}
