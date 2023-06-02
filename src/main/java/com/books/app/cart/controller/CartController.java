package com.books.app.cart.controller;

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
import com.books.app.cart.entity.CartItem;
import com.books.app.cart.service.CartService;
import com.books.app.member.entity.Member;
import com.books.app.product.entity.Product;
import com.books.app.security.dto.MemberContext;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

	private final CartService cartService;
	private final Rq rq;


	@PreAuthorize("isAuthenticated()")
	@GetMapping()
	public String showItems(@AuthenticationPrincipal MemberContext memberContext, Model model) {
		Member buyer = memberContext.getMember();

		List<CartItem> items = cartService.getItemsByBuyer(buyer);

		model.addAttribute("items", items);

		return "cart/items";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/addItem/{productId}")
	public String addItem(@PathVariable Long productId) {
		cartService.addItem(rq.getMember(), new Product(productId));

		return rq.redirectToBackWithMsg("장바구니에 추가되었습니다.");
	}


}
