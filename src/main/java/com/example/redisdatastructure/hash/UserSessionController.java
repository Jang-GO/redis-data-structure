package com.example.redisdatastructure.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class UserSessionController {

    private final UserSessionService sessionService;

    // --- DTOs ---
    public record CreateSessionRequest(String userId, String username) {}
    public record UpdatePageRequest(String page) {}

    /**
     * 세션 생성 API
     * POST /sessions
     * Body: {"userId": "user123", "username": "Alice"}
     */
    @PostMapping
    public ResponseEntity<UserSession> createSession(@RequestBody CreateSessionRequest request) {
        UserSession session = sessionService.createSession(request.userId(), request.username());
        return ResponseEntity.ok(session);
    }

    /**
     * 세션 ID로 조회 API
     * GET /sessions/{sessionId}
     */
    @GetMapping("/{sessionId}")
    public ResponseEntity<UserSession> getSession(@PathVariable String sessionId) {
        UserSession session = sessionService.getSessionBySessionId(sessionId);
        return session != null ? ResponseEntity.ok(session) : ResponseEntity.notFound().build();
    }

    /**
     * 사용자 ID로 조회 API (@Indexed 활용)
     * GET /sessions/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserSession> getSessionByUserId(@PathVariable String userId) {
        UserSession session = sessionService.getSessionByUserId(userId);
        return session != null ? ResponseEntity.ok(session) : ResponseEntity.notFound().build();
    }

    /**
     * 마지막 접속 페이지 갱신 API
     * PUT /sessions/{sessionId}/page
     * Body: {"page": "/products/456"}
     */
    @PutMapping("/{sessionId}/page")
    public ResponseEntity<Void> updateLastAccessPage(@PathVariable String sessionId, @RequestBody UpdatePageRequest request) {
        sessionService.updateLastAccessPage(sessionId, request.page());
        // 성공적으로 처리되었음을 200 OK로 응답
        return ResponseEntity.ok().build();
    }
}
