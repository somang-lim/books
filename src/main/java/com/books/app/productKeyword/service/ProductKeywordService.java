package com.books.app.productKeyword.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.productKeyword.entity.ProductKeyword;
import com.books.app.productKeyword.repository.ProductKeywordRepository;
import com.books.app.productTag.entity.ProductTag;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductKeywordService {
	private final ProductKeywordRepository productKeywordRepository;

	public ProductKeyword getProductKeyword(ProductTag productTag) {
		Optional<ProductKeyword> optProductKeyword = productKeywordRepository.findById(productTag.getProductKeyword().getId());

		if (optProductKeyword.isPresent()) {
			return optProductKeyword.get();
		}

		return null;
	}

	@Transactional
	public ProductKeyword save(String content) {
		Optional<ProductKeyword> optKeyword = productKeywordRepository.findByContent(content);

		if (optKeyword.isPresent()) {
			return optKeyword.get();
		}

		ProductKeyword productKeyword = ProductKeyword
			.builder()
			.content(content)
			.build();

		productKeywordRepository.save(productKeyword);

		return productKeyword;
	}

	@Transactional
	public void remove(List<ProductKeyword> productKeywords) {
		for (ProductKeyword productKeyword : productKeywords) {
			productKeywordRepository.delete(productKeyword);
		}
	}
}
