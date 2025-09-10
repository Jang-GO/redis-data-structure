package com.example.redisdatastructure.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/hash")
@RequiredArgsConstructor
public class RedisHashController {

    private final RedisHashService redisHashService;

    // HSET: 특정 필드에 값을 저장 (JSON body로 {"value": "someValue"} 또는 {"value": 100} 전달)
    @PostMapping("/{key}/{field}")
    public ResponseEntity<Void> hset(@PathVariable String key, @PathVariable String field, @RequestBody Map<String, Object> body) {
        redisHashService.hset(key, field, body.get("value"));
        return ResponseEntity.ok().build();
    }

    // HGET: 특정 필드의 값을 조회
    @GetMapping("/{key}/{field}")
    public ResponseEntity<Object> hget(@PathVariable String key, @PathVariable String field) {
        Object value = redisHashService.hget(key, field);
        return ResponseEntity.ok(value);
    }

    // HGETALL: 모든 필드와 값을 조회
    @GetMapping("/{key}")
    public ResponseEntity<Map<Object, Object>> hgetAll(@PathVariable String key) {
        Map<Object, Object> values = redisHashService.hgetAll(key);
        return ResponseEntity.ok(values);
    }

    // HINCRBY: 필드의 값을 N만큼 증가
    @PostMapping("/{key}/{field}/increment")
    public ResponseEntity<Long> hincrby(@PathVariable String key, @PathVariable String field, @RequestParam(defaultValue = "1") long amount) {
        Long result = redisHashService.hincrby(key, field, amount);
        return ResponseEntity.ok(result);
    }

    // HEXPIRE: 필드에 만료 시간(초) 설정
    @PostMapping("/{key}/{field}/expire")
    public ResponseEntity<Boolean> hexpire(@PathVariable String key, @PathVariable String field, @RequestParam long seconds) {
        Boolean result = redisHashService.hexpire(key, field, seconds);
        return ResponseEntity.ok(result);
    }

    // HTTL: 필드의 남은 만료 시간(초) 조회
    @GetMapping("/{key}/{field}/ttl")
    public ResponseEntity<Long> httl(@PathVariable String key, @PathVariable String field) {
        Long ttl = redisHashService.httl(key, field);
        return ResponseEntity.ok(ttl);
    }
}
