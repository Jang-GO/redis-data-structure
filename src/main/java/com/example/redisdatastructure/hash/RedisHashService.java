package com.example.redisdatastructure.hash;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisHashService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void hset(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public Object hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    public List<Object> hmget(String key, List<String> fields) {
        return redisTemplate.opsForHash().multiGet(key, List.copyOf(fields));
    }

    public Map<Object, Object> hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public Long hlen(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    public Boolean hexists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    public Set<Object> hkeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    public List<Object> hvals(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    public Long hincrby(String key, String field, long value) {
        return redisTemplate.opsForHash().increment(key, field, value);
    }
}
