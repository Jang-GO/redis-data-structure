package com.example.redisdatastructure.string.like;

import com.example.redisdatastructure.string.cache.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final StringRedisTemplate redisTemplate;
    private final ProductRepository productRepository;

    public void increaseLikeCount(Long productId) {
        String key = "product:like:" + productId;
        redisTemplate.opsForValue().increment(key);
    }

    @Transactional
    public void increaseRDBLikeCount(Long productId) {
        productRepository.increaseLikeCount(productId);
    }

    public Long getLikeCount(Long productId) {
        String key = "product:like:" + productId;
        String count = redisTemplate.opsForValue().get(key);

        return Long.parseLong(count != null ? count : "0");
    }
}
