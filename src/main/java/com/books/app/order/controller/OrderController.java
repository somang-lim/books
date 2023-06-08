package com.books.app.order.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.books.app.base.rq.Rq;
import com.books.app.member.entity.Member;
import com.books.app.member.service.MemberService;
import com.books.app.order.entity.Order;
import com.books.app.order.exception.ActorCanNotSeeException;
import com.books.app.order.service.OrderService;
import com.books.app.security.dto.MemberContext;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	private final MemberService memberService;
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

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}")
	public String showDetail(@PathVariable Long id, @AuthenticationPrincipal MemberContext memberContext, Model model) {
		Order order = orderService.findForPrintById(id).orElse(null);

		if (order == null) {
			return rq.redirectToBackWithMsg("주문을 찾을 수 없습니다.");
		}

		Member actor = memberContext.getMember();


		if (orderService.actorCanSee(actor, order) == false) {
			throw new ActorCanNotSeeException();
		}

		model.addAttribute("order", order);

		return "order/detail";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/list")
	public String showList(Model model) {
		List<Order> orders = orderService.findAllByBuyerId(rq.getId());

		model.addAttribute("orders", orders);

		return "order/list";
	}

}
