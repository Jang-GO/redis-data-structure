package com.example.redisdatastructure.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final UserSessionRepository sessionRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 새로운 사용자 세션을 생성하고 Redis에 저장합니다.
     */
    public UserSession createSession(String userId, String username) {
        String sessionId = UUID.randomUUID().toString();
        UserSession newSession = new UserSession(sessionId, userId, username, "/home");
        return sessionRepository.save(newSession);
    }

    /**
     * 세션 ID로 세션 정보를 조회합니다.
     */
    public UserSession getSessionBySessionId(String sessionId) {
        return sessionRepository.findById(sessionId).orElse(null);
    }

    /**
     * 사용자 ID로 세션 정보를 조회합니다. (@Indexed 기능 활용)
     */
    public UserSession getSessionByUserId(String userId) {
        return sessionRepository.findByUserId(userId).orElse(null);
    }

    public void updateLastAccessPage(String sessionId, String page) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.changeLastAccessPage(page);
            sessionRepository.save(session);  // 전체 객체 다시 저장
        });
    }


    public void updateLastAccessPageDirectly(String sessionId, String page) {
        String redisKey = "user_session:" + sessionId;
        redisTemplate.opsForHash().put(redisKey, "lastAccessPage", page);
    }


    public boolean isValidSession(String sessionId) {
        return sessionRepository.existsById(sessionId);
    }

    public void deleteUserSessions(String userId) {
        sessionRepository.findByUserId(userId).ifPresent(sessionRepository::delete);
    }
}