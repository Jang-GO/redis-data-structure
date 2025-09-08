package com.example.redisdatastructure.list;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityFeedService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "feed:";
    private static final int MAX_FEED_SIZE = 100;

    // 새로운 활동 로그를 추가한다.
    public void addLog(Long userId, String activity) {
        String key = KEY_PREFIX + userId;
        ActivityLog log = new ActivityLog(activity, LocalDateTime.now());

        redisTemplate.opsForList().leftPush(key, log);
        redisTemplate.opsForList().trim(key, 0, MAX_FEED_SIZE - 1);
    }

    // 최근 활동 로그를 조회한다.
    public List<Object> getLogs(Long userId, int count) {
        String key = KEY_PREFIX + userId;
        return redisTemplate.opsForList().range(key, 0, count - 1);
    }

    private record ActivityLog(String activity, LocalDateTime timestamp) {}
}
