package com.example.redisdatastructure.list;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageQueueService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String QUEUE_KEY = "job:queue";

    public void submitJob(JobPayload job) {
        redisTemplate.opsForList().leftPush(QUEUE_KEY, job);
    }
}
