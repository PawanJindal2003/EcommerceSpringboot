package com.Project.ecommerce.security.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisTokenService {
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeToken(String token, long durationMillis) {
        redisTemplate.opsForValue().set(token, "valid", Duration.ofMillis(durationMillis));
    }

    public boolean isTokenValid(String token) {
        return "valid".equals(redisTemplate.opsForValue().get(token));
    }

    public void revokeToken(String token) {
        redisTemplate.delete(token);
    }
}
