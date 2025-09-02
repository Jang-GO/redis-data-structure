package com.example.redisdatastructure.string;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisStringService {

    private final StringRedisTemplate redisTemplate;

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setWithTtl(String key, String value, long seconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(seconds));
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Long incrementBy(String key, long amount) {
        return redisTemplate.opsForValue().increment(key, amount);
    }

    public void multiSet(Map<String, String> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    public List<String> multiGet(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    public Boolean setIfAbsent(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public Integer append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }

    public Long strlen(String key) {
        return redisTemplate.opsForValue().size(key);
    }

}
