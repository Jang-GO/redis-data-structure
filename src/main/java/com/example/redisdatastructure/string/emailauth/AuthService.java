package com.example.redisdatastructure.string.emailauth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StringRedisTemplate redisTemplate;
    private static final String AUTH_KEY_PREFIX = "auth:";

    // 6자리 인증번호를 생성하고 3분간 저장
    public void createAuthCode(String email) {
        String authCode = generateRandomCode();
        String key = AUTH_KEY_PREFIX + email;
        // SET auth:user@email.com 123456 EX 180
        redisTemplate.opsForValue().set(key, authCode, Duration.ofMinutes(3));

        // 실제로는 사용자에게 authCode를 이메일 등으로 발송하는 로직
        System.out.println("이메일(" + email + ")로 인증번호 " + authCode + "를 발송했습니다.");
    }

    // 사용자가 입력한 코드를 검증
    public boolean verifyAuthCode(String email, String codeToVerify) {
        String key = AUTH_KEY_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        // 코드가 존재하고, 입력한 코드와 일치하면 검증 성공
        // 코드가 null이면 만료되었거나 발급된 적이 없는 것
        return storedCode != null && storedCode.equals(codeToVerify);
    }

    private String generateRandomCode() {
        // 6자리 랜덤 숫자 생성 로직
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
