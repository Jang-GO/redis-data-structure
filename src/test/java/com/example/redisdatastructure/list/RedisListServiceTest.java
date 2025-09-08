package com.example.redisdatastructure.list;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisListServiceTest {

    @Autowired
    private RedisListService redisListService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 각 테스트가 끝난 후 모든 키를 삭제하여 테스트 격리 보장
    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("List를 스택(Stack)처럼 사용한다 (LIFO).")
    void testListAsStack() {
        // given: 데이터를 왼쪽에 차례로 PUSH (C -> B -> A 순으로 삽입)
        String key = "stack";
        redisListService.lpush(key, "A"); // 현재 리스트: [A]
        redisListService.lpush(key, "B"); // 현재 리스트: [B, A]
        redisListService.lpush(key, "C"); // 현재 리스트: [C, B, A]

        // when & then: 왼쪽에서 POP하면 나중에 넣은 데이터(C)부터 나온다.
        assertThat(redisListService.lpop(key)).isEqualTo("C");
        assertThat(redisListService.lpop(key)).isEqualTo("B");
        assertThat(redisListService.lpop(key)).isEqualTo("A");
        assertThat(redisListService.llen(key)).isEqualTo(0);
    }

    @Test
    @DisplayName("List를 큐(Queue)처럼 사용한다 (FIFO).")
    void testListAsQueue() {
        // given: 데이터를 왼쪽에 차례로 PUSH (C -> B -> A 순으로 삽입)
        String key = "queue";
        redisListService.lpush(key, "A"); // 현재 리스트: [A]
        redisListService.lpush(key, "B"); // 현재 리스트: [B, A]
        redisListService.lpush(key, "C"); // 현재 리스트: [C, B, A]

        // when & then: 오른쪽에서 POP하면 처음에 넣은 데이터(A)부터 나온다.
        assertThat(redisListService.rpop(key)).isEqualTo("A");
        assertThat(redisListService.rpop(key)).isEqualTo("B");
        assertThat(redisListService.rpop(key)).isEqualTo("C");
        assertThat(redisListService.llen(key)).isEqualTo(0);
    }

    @Test
    @DisplayName("LRANGE로 데이터를 삭제하지 않고 조회한다.")
    void testLrange() {
        // given
        String key = "items";
        redisListService.rpush(key, "item1");
        redisListService.rpush(key, "item2");
        redisListService.rpush(key, "item3");

        // when: 인덱스 0부터 1까지 조회
        List<Object> items = redisListService.lrange(key, 0, 1);

        // then
        assertThat(items).containsExactly("item1", "item2");
        // LRANGE는 데이터를 삭제하지 않으므로 길이는 그대로 3
        assertThat(redisListService.llen(key)).isEqualTo(3);
    }

    @Test
    @DisplayName("LTRIM으로 최신 데이터 3개만 유지하는 Capped Collection을 구현한다.")
    void testLtrimAsCappedCollection() {
        // given
        String key = "activity:log";
        redisListService.lpush(key, "Activity 1");
        redisListService.lpush(key, "Activity 2");
        redisListService.lpush(key, "Activity 3");
        redisListService.lpush(key, "Activity 4");
        redisListService.lpush(key, "Activity 5");

        assertThat(redisListService.llen(key)).isEqualTo(5);

        redisListService.ltrim(key, 0, 2);

        assertThat(redisListService.llen(key)).isEqualTo(3);
        List<Object> logs = redisListService.lrange(key, 0, -1);
        assertThat(logs).containsExactly("Activity 5", "Activity 4", "Activity 3");
    }
}