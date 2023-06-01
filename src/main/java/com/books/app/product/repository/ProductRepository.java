package com.books.app.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
