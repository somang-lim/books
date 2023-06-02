package com.books.app.cart.entity;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.books.app.base.entity.BaseEntity;
import com.books.app.member.entity.Member;
import com.books.app.product.entity.Product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class CartItem extends BaseEntity {
	@ManyToOne(fetch = LAZY)
	private Member buyer;

	@ManyToOne(fetch = LAZY)
	private Product product;

	public CartItem(Long id) {
		super(id);
	}
}
