package com.books.app.productTag.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.books.app.base.entity.BaseEntity;
import com.books.app.member.entity.Member;
import com.books.app.product.entity.Product;
import com.books.app.productKeyword.entity.ProductKeyword;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class ProductTag extends BaseEntity {

	@ManyToOne
	@ToString.Exclude
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Product product;

	@ManyToOne
	@ToString.Exclude
	private Member member;

	@ManyToOne
	@ToString.Exclude
	private ProductKeyword productKeyword;

}
