package com.Project.ecommerce.services.scheduler;

import com.Project.ecommerce.repositories.Jwt.BlacklistedAccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BlacklistedTokenCleanUpService {
    private final BlacklistedAccessTokenRepository blacklistedAccessTokenRepository;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void cleanBlacklistedTokens(){
        Instant now = Instant.now();
        blacklistedAccessTokenRepository.deleteByExpiryTimeBefore(now);
    }
}
