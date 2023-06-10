package com.books.app.myBook.entity;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.books.app.base.entity.BaseEntity;
import com.books.app.member.entity.Member;
import com.books.app.order.entity.OrderItem;
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
public class MyBook extends BaseEntity {

	@ManyToOne(fetch = LAZY)
	@ToString.Exclude
	private Member owner;

	@ManyToOne(fetch = LAZY)
	@ToString.Exclude
	private Product product;

	@ManyToOne(fetch = LAZY)
	@ToString.Exclude
	private OrderItem orderItem;

}
