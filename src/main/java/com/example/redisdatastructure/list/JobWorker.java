package com.example.redisdatastructure.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class JobWorker {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String QUEUE_KEY = "job:queue";
    private final ObjectMapper objectMapper;

    // 스레드에 안전한 큐를 사용하여 처리된 작업을 저장 (테스트 검증용)
    @Getter
    private final Queue<JobPayload> processedJobs = new ConcurrentLinkedQueue<>();

    // 애플리케이션 시작 후 별도 스레드에서 작업을 계속 확인
    @PostConstruct
    public void startWorker() {
        new Thread(() -> {
            while (true) {
                Object job = redisTemplate.opsForList().rightPop(QUEUE_KEY, Duration.ZERO);

                if (job != null) {
                    processJob(job);
                }
            }
        }).start();
    }

    private void processJob(Object job) {
        JobPayload jobPayload = objectMapper.convertValue(job, JobPayload.class);
        System.out.println("Processing job: " + job.toString());
        processedJobs.add(jobPayload); // 처리된 작업을 큐에 추가
    }
}
