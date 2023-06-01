package com.books.app.productTag.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.product.entity.Product;
import com.books.app.productKeyword.entity.ProductKeyword;
import com.books.app.productKeyword.service.ProductKeywordService;
import com.books.app.productTag.entity.ProductTag;
import com.books.app.productTag.repository.ProductTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductTagService {

	private final ProductTagRepository productTagRepository;
	private final ProductKeywordService productKeywordService;

	@Transactional
	public void applyProductTags(Product product, String productTagContents) {
		// 1. 기존 태그 가져오기
		List<ProductTag> oldProductTags = getProductTags(product);

		// 2. 새로운 태그 키워드 리스트화
		List<String> productKeywordContents = Arrays.stream(productTagContents.split("#"))
			.map(String::trim)
			.filter(s -> s.length() > 0)
			.collect(Collectors.toList());

		// 3. 삭제할 태그 찾기 (기존 태그 리스트에서 새로운 태그 리스트에 없는 것)
		List<ProductTag> needToDeleteTags = new ArrayList<>();
		for (ProductTag oldProductTag : oldProductTags) {
			// 3-1. 기존에 등록된 태그가 새롭게 등록된 태그에 포함됐는지 여부 확인하기
			boolean contains = productKeywordContents.stream()
				.anyMatch(s -> s.equals(oldProductTag.getProductKeyword().getContent()));

			// 3-2. 없다면, 삭제할 태그 리스트에 추가하기
			if (!contains) {
				needToDeleteTags.add(oldProductTag);
			}
		}

		// 4. 삭제할 키워드 찾기
		List<ProductKeyword> needToDeleteKeywords = new ArrayList<>();
		for (ProductTag productTag : needToDeleteTags) {
			// 4-1. 도서의 키워드 하나 가져오기
			ProductKeyword productKeyword = productKeywordService.getProductKeyword(productTag);

			// 4-2. 키워드가 없으면 멈추기
			if (productKeyword == null) {
				break;
			}

			// 4-3. 키워드가 몇 개 있는지 확인하기
			List<ProductTag> productTags = productTagRepository.findByProductKeywordId(productKeyword.getId());

			// 4-4. 키워드가 1개만 있으면, 삭제할 키워드 리스트에 추가하기
			if (productTags.size() == 1) {
				needToDeleteKeywords.add(productTag.getProductKeyword());
			}
		}

		// 5. 사용하지 않는 태그가 있다면 키워드 먼저 삭제하기
		if (needToDeleteKeywords.size() > 0) {
			deleteProductKeyword(needToDeleteKeywords);
		}

		// 6. 삭제할 태그 리스트에서 하나씩 삭제하기
		needToDeleteTags.forEach(productTag -> productTagRepository.delete(productTag));

		// 7. 새로운 태그 저장하기
		productKeywordContents.forEach(productKeywordContent -> saveProductTag(product, productKeywordContent));

	}

	// 새로운 태그 저장
	private ProductTag saveProductTag(Product product, String productKeywordContent) {
		ProductKeyword productKeyword = productKeywordService.save(productKeywordContent);

		Optional<ProductTag> opProductTag = productTagRepository.findByProductIdAndProductKeywordId(product.getId(), productKeyword.getId());

		if (opProductTag.isPresent()) {
			return opProductTag.get();
		}

		ProductTag productTag = ProductTag
			.builder()
			.product(product)
			.member(product.getAuthor())
			.productKeyword(productKeyword)
			.build();

		productTagRepository.save(productTag);

		return productTag;
	}

	private void deleteProductKeyword(List<ProductKeyword> productKeywords) {
		productKeywordService.remove(productKeywords);
	}

	public List<ProductTag> getProductTags(Product product) {
		return productTagRepository.findAllByProductId(product.getId());
	}
}
