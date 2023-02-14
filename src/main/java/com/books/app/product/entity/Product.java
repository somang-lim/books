package com.books.app.product.entity;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.books.app.base.entity.BaseEntity;
import com.books.app.member.entity.Member;
import com.books.app.postKeyword.entity.PostKeyword;

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
}
