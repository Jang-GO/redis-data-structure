package com.example.redisdatastructure.hash;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserSessionServiceTest {

    @Autowired
    private UserSessionService sessionService;

    @Autowired
    private UserSessionRepository sessionRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("@Indexed 필드를 이용한 커스텀 조회가 동작한다.")
    void findByUserId_test() {
        // given
        String userId = "user123";
        UserSession createdSession = sessionService.createSession(userId, "Alice");

        // when
        UserSession foundSession = sessionService.getSessionByUserId(userId);

        // then
        assertThat(foundSession).isNotNull();
        assertThat(foundSession.getSessionId()).isEqualTo(createdSession.getSessionId());
    }

    @Test
    @DisplayName("PartialUpdate로 특정 필드만 수정할 수 있다.")
    void partialUpdate_test() {
        // given
        UserSession createdSession = sessionService.createSession("user456", "Bob");
        String sessionId = createdSession.getSessionId();

        // when: 마지막 접근 페이지만 "/products/1"로 수정한다.
        sessionService.updateLastAccessPage(sessionId, "/products/1");

        // then: repository.findById를 통해 객체를 다시 조회한다.
        // 이 과정에서 Redis에 저장된 ""/products/1""이 올바르게 Java String "/products/1"로 역직렬화된다.
        UserSession updatedSession = sessionRepository.findById(sessionId).orElseThrow();

        // lastAccessPage 필드는 변경되었다.
        assertThat(updatedSession.getLastAccessPage()).isEqualTo("/products/1"); // 성공
        // username 필드는 그대로 유지되었다.
        assertThat(updatedSession.getUsername()).isEqualTo("Bob");
    }
}