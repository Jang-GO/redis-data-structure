package com.example.redisdatastructure.string.cache.dto.response;

import com.example.redisdatastructure.string.cache.domain.Product;

public record ProductResponse(Long id, String name, int price) {

    public static ProductResponse from(Product product){
        return new ProductResponse(product.getId(), product.getName(), product.getPrice());
    }
}
