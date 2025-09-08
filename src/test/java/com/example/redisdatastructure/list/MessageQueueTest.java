package com.example.redisdatastructure.list;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class MessageQueueTest {

    @Autowired
    private MessageQueueService messageQueueService;

    @Autowired
    private JobWorker jobWorker;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void tearDown() {
        // 테스트 후 Redis 큐와 워커의 처리 목록을 비워줌
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        jobWorker.getProcessedJobs().clear();
    }

    @Test
    @DisplayName("작업을 큐에 넣으면 비동기 워커가 작업을 처리한다.")
    void testMessageQueue() {
        // given
        JobPayload job = new JobPayload("encoding", "video.mp4");

        // when: 작업을 큐에 제출한다.
        messageQueueService.submitJob(job);

        // then: 워커가 비동기적으로 작업을 처리할 때까지 최대 5초간 기다린다.
        // Awaitility 라이브러리는 Thread.sleep() 없이 안정적으로 비동기 코드를 테스트하게 해준다.
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Queue<JobPayload> processedJobs = jobWorker.getProcessedJobs();
            assertThat(processedJobs).hasSize(1);
            assertThat(processedJobs.peek()).isEqualTo(job);
        });
    }
}