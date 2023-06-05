package com.books.app.order.entity;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.books.app.base.entity.BaseEntity;
import com.books.app.member.entity.Member;

import lombok.Builder;
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
@Table(name = "product_order")
public class Order extends BaseEntity {

	@ManyToOne(fetch = LAZY)
	private Member buyer;

	private String name;

	private boolean isPaid;

	private boolean isCanceled;

	private boolean isRefunded;

	private LocalDateTime payDate;
	private LocalDateTime cancelDate;
	private LocalDateTime refundDate;

	@Builder.Default
	@OneToMany(mappedBy = "order", cascade = ALL, orphanRemoval = true)
	private List<OrderItem> orderItems = new ArrayList<>();


	public void addOrderItem(OrderItem orderItem) {
		orderItem.setOrder(this);

		orderItems.add(orderItem);
	}

	public void makeName() {
		String name = orderItems.get(0).getProduct().getSubject();

		if (orderItems.size() > 1) {
			name += "외 %d권".formatted(orderItems.size() - 1);
		}

		this.name = name;
	}

}
