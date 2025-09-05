package com.example.redisdatastructure.list;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisListService {

    private final StringRedisTemplate redisTemplate;

    public void lpush(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public void rpush(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public String lpop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public String rpop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    public List<String> lrange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public void ltrim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    public Long llen(String key) {
        return redisTemplate.opsForList().size(key);
    }
}
