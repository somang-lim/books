package com.books.app.order.entity;

import static javax.persistence.FetchType.LAZY;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.books.app.base.entity.BaseEntity;
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
public class OrderItem extends BaseEntity {

	@ManyToOne(fetch = LAZY)
	private Order order;

	private LocalDateTime payDate;

	@ManyToOne(fetch = LAZY)
	private Product product;

	// 가격
	private int price; // 권장 판매가
	private int salePrice; // 실제 판매가
	private int wholesalePrice; // 도매가
	private int pgFee; // 결제대행사 수수료
	private int payPrice; // 결제 금액
	private int refundPrice; // 환불 금액
	private boolean isPaid; // 결제 여부

}
