package com.example.redisdatastructure.cache.repository;

import com.example.redisdatastructure.cache.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
