package com.example.redisdatastructure.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ActivityFeedServiceTest {

    @Autowired
    private ActivityFeedService activityFeedService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper; // 객체 변환을 위해 주입

    // 테스트용 간단한 데이터 객체
    private record ActivityLog(String activity, LocalDateTime timestamp) {}

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("새로운 활동 로그를 추가하면 리스트의 맨 앞에 저장된다.")
    void testAddLog() {
        // given
        Long userId = 1L;

        // when
        activityFeedService.addLog(userId, "Logged in");
        activityFeedService.addLog(userId, "Viewed product page");

        // then
        List<Object> logs = activityFeedService.getLogs(userId, 2);

        ActivityLog latestLog = objectMapper.convertValue(logs.get(0), ActivityLog.class);
        ActivityLog previousLog = objectMapper.convertValue(logs.get(1), ActivityLog.class);

        assertThat(logs).hasSize(2);
        assertThat(latestLog.activity()).isEqualTo("Viewed product page");
        assertThat(previousLog.activity()).isEqualTo("Logged in");
    }

    @Test
    @DisplayName("로그가 100개를 초과하면 가장 오래된 로그가 삭제된다.")
    void testFeedSizeLimit() {
        // given
        Long userId = 2L;
        int maxFeedSize = 100;

        // when: 101개의 로그를 추가한다.
        for (int i = 0; i < maxFeedSize + 1; i++) {
            activityFeedService.addLog(userId, "Activity " + i);
        }

        // then
        String key = "feed:" + userId;
        Long size = redisTemplate.opsForList().size(key);
        List<Object> logs = activityFeedService.getLogs(userId, maxFeedSize + 1);

        // 리스트의 크기가 100으로 유지되는지 확인
        assertThat(size).isEqualTo(maxFeedSize);

        // 가장 최근에 추가된 로그("Activity 100")가 맨 앞에 있는지 확인
        ActivityLog latestLog = objectMapper.convertValue(logs.get(0), ActivityLog.class);
        assertThat(latestLog.activity()).isEqualTo("Activity 100");

        // 가장 처음에 추가된 로그("Activity 0")가 삭제되었는지 확인
        ActivityLog oldestLog = objectMapper.convertValue(logs.get(maxFeedSize - 1), ActivityLog.class);
        assertThat(oldestLog.activity()).isNotEqualTo("Activity 0");
        assertThat(oldestLog.activity()).isEqualTo("Activity 1");
    }
}
