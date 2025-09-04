package com.example.redisdatastructure.string.cache.repository;

import com.example.redisdatastructure.string.cache.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
