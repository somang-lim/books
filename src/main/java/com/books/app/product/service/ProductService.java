package com.books.app.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.member.entity.Member;
import com.books.app.postKeyword.entity.PostKeyword;
import com.books.app.postKeyword.service.PostKeywordService;
import com.books.app.product.entity.Product;
import com.books.app.product.form.ProductForm;
import com.books.app.product.repository.ProductRepository;
import com.books.app.productTag.service.ProductTagService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final PostKeywordService postKeywordService;
	private final ProductTagService productTagService;

	@Transactional
	public Product create(Member author, ProductForm productForm) {
		PostKeyword postKeyword = postKeywordService.findById(productForm.getPostKeywordId()).get();

		Product product = Product
			.builder()
			.author(author)
			.postKeyword(postKeyword)
			.subject(productForm.getSubject())
			.price(productForm.getPrice())
			.build();

		productRepository.save(product);

		applyProductTags(product, productForm.getProductTagContents());

		return product;
	}

	private void applyProductTags(Product product, String productTagContents) {
		productTagService.applyProductTags(product, productTagContents);
	}

}
