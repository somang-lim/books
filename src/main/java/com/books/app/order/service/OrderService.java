package com.books.app.order.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.cart.entity.CartItem;
import com.books.app.cart.service.CartService;
import com.books.app.member.entity.Member;
import com.books.app.order.entity.Order;
import com.books.app.order.entity.OrderItem;
import com.books.app.order.repository.OrderRepository;
import com.books.app.product.entity.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

	private final OrderRepository orderRepository;
	private final CartService cartService;


	@Transactional
	public Order createFromCart(Member buyer) {
		// 입력된 회원의 장바구니 아이템들을 전부 가져온다.

		// 특정 장바구니의 상품옵션이 판매할 수 없다면, 삭제
		// 특정 장바구니의 상품옵션이 판매 가능하면 주문 품목으로 옮긴 후, 삭제
		List<CartItem> cartItems = cartService.getItemsByBuyer(buyer);

		List<OrderItem> orderItems = new ArrayList<>();

		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();

			if (product.isOrderable()) {
				orderItems.add(new OrderItem(product));
			}

			cartService.removeItem(cartItem);
		}

		return create(buyer, orderItems);
	}

	@Transactional
	public Order create(Member buyer, List<OrderItem> orderItems) {
		Order order = Order
				.builder()
				.buyer(buyer)
				.build();

		for (OrderItem orderItem : orderItems) {
			order.addOrderItem(orderItem);
		}

		// 주문 품목으로부터 이름을 만든다.
		order.makeName();

		orderRepository.save(order);

		return order;
	}

	public Optional<Order> findForPrintById(Long id) {
		return findById(id);
	}

	public Optional<Order> findById(Long id) {
		return orderRepository.findById(id);
	}

	public boolean actorCanSee(Member actor, Order order) {
		return actor.getId().equals(order.getBuyer().getId());
	}

	public boolean isBehindDeleteButton(Long productId) {
		Order order = orderRepository.findById(productId).orElse(null);

		if (order != null) {
			return false;
		}

		return true;
	}

	public List<Order> findAllByBuyerId(Long buyerId) {
		return orderRepository.findAllByBuyerIdOrderByIdDesc(buyerId);
	}

}
