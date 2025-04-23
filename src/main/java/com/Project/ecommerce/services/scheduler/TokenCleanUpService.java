package com.Project.ecommerce.services.scheduler;

import com.Project.ecommerce.entities.jwt.ActivationToken;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.ResourceNotFoundException;
import com.Project.ecommerce.repositories.Jwt.ActivationTokenRepository;
import com.Project.ecommerce.repositories.Jwt.BlacklistedAccessTokenRepository;
import com.Project.ecommerce.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenCleanUpService {
    private static final Logger logger = LoggerFactory.getLogger(TokenCleanUpService.class);
    private final BlacklistedAccessTokenRepository blacklistedAccessTokenRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void cleanBlacklistedTokens() {
        Instant now = Instant.now();
        logger.info("Running scheduled task: Clean blacklisted tokens before {}", now);

        blacklistedAccessTokenRepository.deleteByExpiryTimeBefore(now);
        logger.info("Blacklisted tokens deleted");
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void cleanActivationTokens() {
        Instant expiryTime = Instant.now().minus(3, ChronoUnit.HOURS);
        logger.info("Running scheduled task: Clean activation tokens created before {}", expiryTime);

        List<ActivationToken> expiredTokens = activationTokenRepository.findAllByCreatedAtBefore(expiryTime);

        for (ActivationToken expiredToken : expiredTokens) {
            String email = expiredToken.getEmail();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            if (!user.getIsActive()) {
                activationTokenRepository.deleteByEmail(email);
            }
        }
        logger.info("Deleted expired activation token for inactive users");
    }
}
