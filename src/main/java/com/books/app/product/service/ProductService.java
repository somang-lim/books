package com.books.app.product.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.member.entity.Member;
import com.books.app.post.entity.Post;
import com.books.app.postKeyword.entity.PostKeyword;
import com.books.app.postKeyword.service.PostKeywordService;
import com.books.app.postTag.entity.PostTag;
import com.books.app.postTag.service.PostTagService;
import com.books.app.product.entity.Product;
import com.books.app.product.form.ProductForm;
import com.books.app.product.repository.ProductRepository;
import com.books.app.productTag.entity.ProductTag;
import com.books.app.productTag.service.ProductTagService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final PostKeywordService postKeywordService;
	private final ProductTagService productTagService;
	private final PostTagService postTagService;

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

	public Optional<Product> findById(Long id) {
		return productRepository.findById(id);
	}

	public Optional<Product> findForPrintById(Long id) {
		Optional<Product> opProduct = findById(id);

		if (opProduct.isEmpty()) return opProduct;

		List<ProductTag> productTags = getProductTags(opProduct.get());

		opProduct.get().getExtra().put("productTags", productTags);

		return opProduct;
	}

	private List<ProductTag> getProductTags(Product product) {
		return productTagService.getProductTags(product);
	}

	public List<Post> findPostsByProduct(Product product) {
		Member author = product.getAuthor();
		PostKeyword postKeyword = product.getPostKeyword();
		List<PostTag> postTags = postTagService.getPostTags(author.getId(), postKeyword.getId());

		return postTags
				.stream()
				.map(PostTag::getPost)
				.collect(Collectors.toList());
	}

	public boolean actorCanModify(Member actor, Product product) {
		if (actor == null) return false;

		return actor.getId().equals(product.getAuthor().getId());
	}

	public boolean actorCanRemove(Member actor, Product product) {
		return actorCanModify(actor, product);
	}

}
