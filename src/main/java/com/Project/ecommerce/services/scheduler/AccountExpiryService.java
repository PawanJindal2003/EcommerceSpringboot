package com.Project.ecommerce.services.scheduler;

import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountExpiryService {
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void setIsExpired(){
        Instant expiryTime = Instant.now().minus(60, ChronoUnit.DAYS);

        List<User> allUsers = userRepository.findAll();
        List<User> updatedUsers = allUsers.stream()
                .filter(user -> user.getPasswordUpdateDate()!=null)
                .filter(user -> user.getPasswordUpdateDate().toInstant().isBefore(expiryTime))
                .filter(user -> Boolean.FALSE.equals(user.getIsExpired()))
                .peek(user -> user.setIsExpired(true))
                .toList();
        userRepository.saveAll(updatedUsers);
    }
}
