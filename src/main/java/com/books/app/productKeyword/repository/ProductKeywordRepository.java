package com.books.app.productKeyword.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.productKeyword.entity.ProductKeyword;

public interface ProductKeywordRepository extends JpaRepository<ProductKeyword, Long> {
	Optional<ProductKeyword> findByContent(String content);
}
