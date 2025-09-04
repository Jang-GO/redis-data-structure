package com.example.redisdatastructure.string.like;

import com.example.redisdatastructure.string.cache.domain.Product;
import com.example.redisdatastructure.string.cache.repository.ProductRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class LikeServiceTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private ProductRepository productRepository; // DB 데이터 생성을 위해 추가

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Long productId;

    // 각 테스트 전에 DB에 상품을 생성
    @BeforeEach
    void setUp() {
        Product product = productRepository.save(new Product("Sample Product", 10000));
        productId = product.getId();
    }

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Redis INCR: 1000개의 동시 요청을 처리한다.")
    void redis_concurrent_like_test() throws InterruptedException {
        // given
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        long startTime = System.nanoTime();
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    likeService.increaseLikeCount(productId);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        long endTime = System.nanoTime();
        long totalDuration = (endTime - startTime) / 1_000_000;
        log.info("총 1000개 Redis 좋아요 요청 처리 시간: {}ms", totalDuration);

        // then
        Long likeCount = likeService.getLikeCount(productId);
        assertThat(likeCount).isEqualTo(1000L);
    }

    @Test
    @DisplayName("RDB @Modifying: 1000개의 동시 요청을 처리한다.")
    void rdb_concurrent_like_test() throws InterruptedException {
        // given
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        long startTime = System.nanoTime();
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    likeService.increaseRDBLikeCount(productId);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        long endTime = System.nanoTime();
        long totalDuration = (endTime - startTime) / 1_000_000;
        log.info("총 1000개 RDB 좋아요 요청 처리 시간: {}ms", totalDuration);

        // then
        Product product = productRepository.findById(productId).orElseThrow();
        assertThat(product.getLikeCount()).isEqualTo(1000L);
    }
}
