package com.example.redisdatastructure.cache.service;

import com.example.redisdatastructure.cache.dto.response.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Long productId;

    // 각 테스트 전에 샘플 데이터를 DB에 저장
    @BeforeEach
    void setUp() {
        productId = productService.save("Sample Product", 10000);
    }

    // 각 테스트가 끝난 후 모든 레디스 키를 삭제
    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("캐싱 적용 시 첫 조회는 DB에서, 두 번째 조회는 캐시에서 가져온다.")
    void testCachingPerformance() throws Exception {
        // 1. 첫 번째 조회 (Cache Miss)
        long startTime1 = System.nanoTime();
        ProductResponse product1 = productService.getProduct(productId);
        long endTime1 = System.nanoTime();
        long duration1 = (endTime1 - startTime1) / 1_000_000;
        log.info("첫 번째 조회 (Cache Miss) 응답 시간: {}ms", duration1);

        // 2. 두 번째 조회 (Cache Hit)
        long startTime2 = System.nanoTime();
        ProductResponse product2 = productService.getProduct(productId);
        long endTime2 = System.nanoTime();
        long duration2 = (endTime2 - startTime2) / 1_000_000;
        log.info("두 번째 조회 (Cache Hit) 응답 시간: {}ms", duration2);
    }

    @Test
    @DisplayName("캐시스템피드 발생")
    void testConcurrentCaching() throws InterruptedException {
        int threadCount = 10;
        // 동시 요청을 위한 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        // 모든 스레드의 작업이 끝날 때까지 대기하기 위한 CountDownLatch
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        long startTime = System.nanoTime();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.getProduct(productId);
                } catch (Exception e) {
                    log.error("Error during concurrent getProduct call", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 메인 스레드는 대기
        long endTime = System.nanoTime();
        long totalDuration = (endTime - startTime) / 1_000_000;
        log.info("총 10개 동시 요청 처리 시간: {}ms", totalDuration);
        }
}