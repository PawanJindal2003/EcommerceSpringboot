package com.Project.ecommerce.repositories.Jwt;

import com.Project.ecommerce.entities.jwt.ForgetPasswordToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForgetPasswordTokenRepository extends JpaRepository<ForgetPasswordToken, String> {
    public Optional<ForgetPasswordToken> findByEmail(String email);
    @Modifying
    @Transactional
    public void deleteByEmail(String email);
}
