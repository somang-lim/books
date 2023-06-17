package com.books.app.product.entity;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.books.app.base.entity.BaseEntity;
import com.books.app.cart.entity.CartItem;
import com.books.app.member.entity.Member;
import com.books.app.postKeyword.entity.PostKeyword;
import com.books.app.productTag.entity.ProductTag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor(access = PROTECTED)
public class Product extends BaseEntity {
	@ManyToOne(fetch = LAZY)
	private Member author;

	@ManyToOne(fetch = LAZY)
	private PostKeyword postKeyword;

	private String subject;

	private int price;


	public Product(Long id) {
		super(id);
	}

	public String getJdenticon() {
		return "product__" + getId();
	}

	public String getExtra_productTagLinks() {
		Map<String, Object> extra = getExtra();

		if (!extra.containsKey("productTags")) {
			return "";
		}

		List<ProductTag> productTags = (List<ProductTag>) extra.get("productTags");

		if (productTags.isEmpty()) {
			return "";
		}

		return productTags
				.stream()
				.map(productTag -> {
					String text = "#" + productTag.getProductKeyword().getContent();

					return """
							<a href="%s" class="text-link">%s</a>
							"""
						.stripIndent()
						.formatted(productTag.getProductKeyword().getListUrl(), text);
				})
				.sorted()
				.collect(Collectors.joining(" "));
	}

	public String getExtra_inputValue_hashTagContents() {
		Map<String, Object> extra = getExtra();

		if (!extra.containsKey("productTags")) {
			return "";
		}

		List<ProductTag> productTags = (List<ProductTag>) extra.get("productTags");

		if (productTags.isEmpty()) {
			return "";
		}

		return productTags
				.stream()
				.map(productTag -> "#" + productTag.getProductKeyword().getContent())
				.sorted()
				.collect(Collectors.joining(" "));
	}

	public CartItem getExtra_actor_cartItem() {
		Map<String, Object> extra = getExtra();

		if (!extra.containsKey("actor_cartItem")) {
			return null;
		}

		return (CartItem) extra.get("actor_cartItem");
	}

	public boolean getExtra_actor_hasInCart() {
		return getExtra_actor_cartItem() != null;
	}

	public boolean isOrderable() {
		return true;
	}

	public int getSalePrice() {
		return getPrice();
	}

	public int getWholesalePrice() {
		return (int) Math.ceil(getPrice() * 0.6);
	}

}
