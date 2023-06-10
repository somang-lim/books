package com.books.app.order.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.base.dto.RsData;
import com.books.app.cart.entity.CartItem;
import com.books.app.cart.service.CartService;
import com.books.app.member.entity.Member;
import com.books.app.member.service.MemberService;
import com.books.app.myBook.service.MyBookService;
import com.books.app.order.entity.Order;
import com.books.app.order.entity.OrderItem;
import com.books.app.order.repository.OrderRepository;
import com.books.app.product.entity.Product;

import ch.qos.logback.core.joran.conditional.IfAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

	private final OrderRepository orderRepository;
	private final CartService cartService;
	private final MemberService memberService;
	private final MyBookService myBookService;


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

	public boolean isBehindDeleteButton(Long orderId, Long productId) {
		Order order = orderRepository.findByIdAndOrderItems_productId(orderId, productId).orElse(null);

		if (order != null) {
			return false;
		}

		return true;
	}

	public boolean isBehindButton(Long authorId, Long productId) {
		Order order = orderRepository.findByBuyerIdAndOrderItems_productId(authorId, productId).orElse(null);

		if (order != null) {
			return false;
		}

		return true;
	}

	public List<Order> findAllByBuyerId(Long buyerId) {
		return orderRepository.findAllByBuyerIdOrderByIdDesc(buyerId);
	}

	@Transactional
	public RsData payByTossPayments(Order order, long useRestCash) {
		Member buyer = order.getBuyer();
		long restCash = buyer.getRestCash();
		int payPrice = order.calculatePayPrice();

		long pgPayPrice = payPrice - useRestCash;
		memberService.addCash(buyer, pgPayPrice, "주문__%d__충전__토스페이먼츠".formatted(order.getId()));
		memberService.addCash(buyer, pgPayPrice * -1, "주문__%d__사용__토스페이먼츠".formatted(order.getId()));

		if (useRestCash > 0) {
			if (useRestCash > restCash) {
				throw new RuntimeException("예치금이 부족합니다.");
			}

			memberService.addCash(buyer, useRestCash * -1, "주문__%d__사용__예치금".formatted(order.getId()));
			restCash -= useRestCash;
			memberService.setRestCash(buyer, restCash);
		}

		payDone(order);

		return RsData.of("S-1", "결제가 완료되었습니다.");
	}

	@Transactional
	public RsData payByRestCashOnly(Order order) {
		Member buyer = order.getBuyer();
		Long restCash = buyer.getRestCash();
		int payPrice = order.calculatePayPrice();

		if (payPrice > restCash) {
			throw new RuntimeException("예치금이 부족합니다.");
		}

		memberService.addCash(buyer, payPrice * -1, "주문__%d__사용".formatted(order.getId()));

		restCash -= payPrice;
		memberService.setRestCash(buyer, restCash);

		payDone(order);

		return RsData.of("S-1", "결제가 완료되었습니다.");
	}

	private void payDone(Order order) {
		order.setPaymentDone();
		myBookService.add(order);
		orderRepository.save(order);
	}

	@Transactional
	public RsData cancel(Long orderId, Member actor) {
		Order order = findById(orderId).get();

		return cancel(order, actor);
	}

	@Transactional
	public RsData cancel(Order order, Member actor) {
		RsData rsData = actorCanCancel(actor, order);

		if (rsData.isFail()) {
			return rsData;
		}

		order.setCanceled(true);

		return RsData.of("S-1", "취소되었습니다.");
	}

	public boolean actorCanSee(Member actor, Order order) {
		return actor.getId().equals(order.getBuyer().getId());
	}

	public boolean actorCanPayment(Member actor, Order order) {
		return actorCanSee(actor, order);
	}

	public RsData actorCanCancel(Member actor, Order order) {
		if (order.isPaid()) {
			return RsData.of("F-3", "이미 결제가 완료되었습니다.");
		}

		if (order.isCanceled()) {
			return RsData.of("F-1", "이미 취소되었습니다.");
		}

		if (!actor.getId().equals(order.getBuyer().getId())) {
			return RsData.of("F-2", "권한이 없습니다.");
		}

		return RsData.of("S-1", "취소할 수 있습니다.");
	}

}
