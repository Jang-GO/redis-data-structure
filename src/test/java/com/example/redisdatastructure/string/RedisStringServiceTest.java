package com.example.redisdatastructure.string;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;


@SpringBootTest
class StringPracticeServiceTest {

    @Autowired
    private RedisStringService redisService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("기본적인 SET과 GET 명령어를 테스트한다.")
    void testSetAndGet() {
        // given
        String key = "testKey";
        String value = "testValue";

        // when
        redisService.set(key, value);
        String retrievedValue = redisService.get(key);

        // then
        assertThat(retrievedValue).isEqualTo(value);
    }

    @Test
    @DisplayName("만료 시간(TTL)이 있는 SET 명령어를 테스트한다.")
    void testSetWithTtl() throws InterruptedException {
        // given
        String key = "ttlKey";
        String value = "disappear";
        long ttlSeconds = 2L;

        // when
        redisService.setWithTtl(key, value, ttlSeconds);

        // then
        String retrievedValue1 = redisService.get(key);
        assertThat(retrievedValue1).isEqualTo(value); // 아직 살아있음

        Thread.sleep(Duration.ofSeconds(ttlSeconds).toMillis()); // 만료 시간까지 대기

        String retrievedValue2 = redisService.get(key);
        assertThat(retrievedValue2).isNull(); // 만료 후에는 null
    }

    @Test
    @DisplayName("원자적인 INCR 명령어를 테스트한다.")
    void testIncrement() {
        // given
        String key = "counter";

        // when
        Long count1 = redisService.increment(key); // 1
        Long count2 = redisService.increment(key); // 2
        Long count3 = redisService.incrementBy(key, 5); // 7

        // then
        assertThat(count1).isEqualTo(1L);
        assertThat(count2).isEqualTo(2L);
        assertThat(count3).isEqualTo(7L);
        assertThat(redisService.get(key)).isEqualTo("7");
    }

    @Test
    @DisplayName("여러 키를 한 번에 처리하는 MSET, MGET을 테스트한다.")
    void testMultiSetAndGet() {
        // given
        Map<String, String> data = Map.of(
                "user:1", "a",
                "user:2", "b",
                "user:3", "c"
        );

        // when
        redisService.multiSet(data);
        List<String> retrievedValues = redisService.multiGet(List.of("user:1", "user:3", "user:4"));

        // then
        assertThat(retrievedValues).hasSize(3);
        assertThat(retrievedValues.get(0)).isEqualTo("a");
        assertThat(retrievedValues.get(1)).isEqualTo("c");
        assertThat(retrievedValues.get(2)).isNull(); // 존재하지 않는 키
    }

    @Test
    @DisplayName("키가 없을 때만 저장되는 SETNX(setIfAbsent)를 테스트한다.")
    void testSetIfAbsent() {
        // given
        String key = "event:lock";

        // when
        Boolean success = redisService.setIfAbsent(key, "user1"); // 처음 시도
        Boolean failure = redisService.setIfAbsent(key, "user2"); // 두 번째 시도

        // then
        assertThat(success).isTrue();
        assertThat(failure).isFalse();
        assertThat(redisService.get(key)).isEqualTo("user1"); // user1이 선점
    }
}