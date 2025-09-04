package com.example.redisdatastructure.string.cache.service;

import com.example.redisdatastructure.string.cache.domain.Product;
import com.example.redisdatastructure.string.cache.dto.response.ProductResponse;
import com.example.redisdatastructure.string.cache.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long save(String name, int price){
        Product product = new Product(name, price);
        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    public ProductResponse getProduct(Long productId) throws JsonProcessingException{
        String key = getKey(productId);

        String cachedJson = redisTemplate.opsForValue().get(key);

        if(cachedJson != null){
            log.info("캐시 히트 ID: {}", productId);
            return objectMapper.readValue(cachedJson, ProductResponse.class);
        }

        log.info("캐시 미스 ID: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("상품 번호에 해당하는 상품이 없습니다."));

        ProductResponse response = ProductResponse.from(product);
        String productJson = objectMapper.writeValueAsString(response);

        redisTemplate.opsForValue().set(key, productJson, Duration.ofMinutes(10));

        return response;

    }

    private String getKey(Long productId) {
        return "product:" + productId;
    }
}
