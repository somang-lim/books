package com.books.app.product.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.cart.entity.CartItem;
import com.books.app.cart.service.CartService;
import com.books.app.member.entity.Member;
import com.books.app.post.entity.Post;
import com.books.app.postKeyword.entity.PostKeyword;
import com.books.app.postKeyword.service.PostKeywordService;
import com.books.app.postTag.entity.PostTag;
import com.books.app.postTag.service.PostTagService;
import com.books.app.product.entity.Product;
import com.books.app.product.form.ProductForm;
import com.books.app.product.form.ProductModifyForm;
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
	private final CartService cartService;


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

	@Transactional
	public void modify(Product product, ProductModifyForm productForm) {
		product.setSubject(productForm.getSubject());
		product.setPrice(productForm.getPrice());

		applyProductTags(product, productForm.getProductTagContents());
	}

	public Optional<Product> findById(Long id) {
		return productRepository.findById(id);
	}

	public Optional<Product> findForPrintById(Long id, Member actor) {
		Optional<Product> opProduct = findById(id);

		if (opProduct.isEmpty()) return opProduct;

		loadForPrintData(opProduct.get(), actor);

		return opProduct;
	}

	private List<ProductTag> getProductTags(Product product) {
		return productTagService.getProductTags(product);
	}

	public List<ProductTag> getProductTags(String productTagContent, Member actor) {
		List<ProductTag> productTags = productTagService.getProductTags(productTagContent);

		loadForPrintDataOnProductTagList(productTags, actor);

		return productTags;
	}

	private void loadForPrintDataOnProductTagList(List<ProductTag> productTags, Member actor) {
		List<Product> products = productTags
				.stream()
				.map(ProductTag::getProduct)
				.collect(toList());

		loadForPrintData(products, actor);
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

	public List<Product> findAllForPrintByOrderByIdDesc(Member actor) {
		List<Product> products = findAllByOrderByIdDesc();

		loadForPrintData(products, actor);

		return products;

	}

	public List<Product> findAllByOrderByIdDesc() {
		return productRepository.findAllByOrderByIdDesc();
	}

	private void loadForPrintData(Product product, Member actor) {
		loadForPrintData(List.of(product), actor);
	}

	public void loadForPrintData(List<Product> products, Member actor) {
		long[] ids = products
				.stream()
				.mapToLong(Product::getId)
				.toArray();

		List<ProductTag> productTagsByProductIds = productTagService.getProductTagsByProductIdIn(ids);

		// 현재 로그인 상태이고 장바구니에 품목이 추가된 상태인지
		if (actor != null) {
			List<CartItem> cartItems = cartService.getCartItemsByBuyerIdProductIdIn(actor.getId(), ids);

			Map<Long, CartItem> cartItemsByProductIdMap = cartItems
				.stream()
				.collect(toMap(
					cartItem ->  cartItem.getProduct().getId(),
					cartItem -> cartItem
				));

			products.stream()
					.filter(product -> cartItemsByProductIdMap.containsKey(product.getId()))
					.map(product -> cartItemsByProductIdMap.get(product.getId()))
					.forEach(cartItem -> cartItem.getProduct().getExtra().put("actor_cartItem", cartItem));
		}

		Map<Long, List<ProductTag>> productTagsByProductIdMap = productTagsByProductIds.stream()
				.collect(groupingBy(
					productTag -> productTag.getProduct().getId(), toList()
				));

		products.stream().forEach(product -> {
			List<ProductTag> productTags = productTagsByProductIdMap.get(product.getId());

			if (productTags == null || productTags.size() == 0) return;

			product.getExtra().put("productTags", productTags);
		});

	}

	public boolean actorCanModify(Member actor, Product product) {
		if (actor == null) return false;

		return actor.getId().equals(product.getAuthor().getId());
	}

	public boolean actorCanRemove(Member actor, Product product) {
		return actorCanModify(actor, product);
	}

	@Transactional
	public void remove(Product product) {
		if (product.getExtra().containsKey("productTags")) {
			productTagService.remove(product);
		}

		productRepository.delete(product);
	}

	public Product detail(Long id) {
		Optional<Product> opProduct = findById(id);

		if (opProduct.isEmpty()) {
			return opProduct.get();
		}

		Product product = opProduct.get();

		List<ProductTag> productTags = getProductTags(product);

		product.getExtra().put("productTags", productTags);

		return product;
	}
}
