package com.books.app.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findAllByOrderByIdDesc();
}
