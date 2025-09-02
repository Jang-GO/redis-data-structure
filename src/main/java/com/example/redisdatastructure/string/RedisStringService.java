package com.example.redisdatastructure.string;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisStringService {

    private final StringRedisTemplate redisTemplate;

    /**
     * SET: 기본적으로 값을 저장합니다.
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * SET [EX]: 만료 시간을 설정하여 값을 저장합니다.
     */
    public void setWithTtl(String key, String value, long seconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(seconds));
    }

    /**
     * GET: 키로 값을 조회합니다.
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // --- 2. 원자적(Atomic) 카운터 명령어 실습 ---

    /**
     * INCR: 키의 값을 1 증가시킵니다.
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * INCRBY: 키의 값을 N만큼 증가시킵니다.
     */
    public Long incrementBy(String key, long amount) {
        return redisTemplate.opsForValue().increment(key, amount);
    }

    // --- 3. Multi-key 처리(Bulk) 명령어 실습 ---

    /**
     * MSET: 여러 키-값 쌍을 한 번에 저장합니다.
     */
    public void multiSet(Map<String, String> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * MGET: 여러 키의 값을 한 번에 조회합니다.
     */
    public List<String> multiGet(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    // --- 4. 조건부 & 기타 명령어 실습 ---

    /**
     * SET [NX]: 키가 존재하지 않을 때만 값을 저장합니다. (SETNX와 동일)
     * @return 성공 시 true, 실패(키가 이미 존재) 시 false
     */
    public Boolean setIfAbsent(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * APPEND: 기존 값 뒤에 문자열을 이어 붙입니다.
     */
    public Integer append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }

    /**
     * STRLEN: 문자열의 길이를 반환합니다.
     */
    public Long strlen(String key) {
        return redisTemplate.opsForValue().size(key);
    }

}
