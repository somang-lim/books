package com.books.app.order.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.books.app.base.rq.Rq;
import com.books.app.member.entity.Member;
import com.books.app.order.entity.Order;
import com.books.app.order.service.OrderService;
import com.books.app.security.dto.MemberContext;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	private final Rq rq;


	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String create(@AuthenticationPrincipal MemberContext memberContext) {
		Member buyer = memberContext.getMember();
		Order order = orderService.createFromCart(buyer);

		return rq.redirectWithMsg(
			"/order/%d".formatted(order.getId()),
			"%d번 주문이 생성되었습니다.".formatted(order.getId())
		);
	}

}
