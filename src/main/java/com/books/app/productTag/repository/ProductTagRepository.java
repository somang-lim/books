package com.books.app.productTag.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.productTag.entity.ProductTag;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {
	List<ProductTag> findAllByProductId(Long productId);

	List<ProductTag> findByProductKeywordId(Long productKeywordId);

	Optional<ProductTag> findByProductIdAndProductKeywordId(Long productId, Long productKeywordId);
}
