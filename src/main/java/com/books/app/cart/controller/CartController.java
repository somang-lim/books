package com.books.app.cart.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.books.app.base.rq.Rq;
import com.books.app.cart.service.CartService;
import com.books.app.product.entity.Product;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

	private final CartService cartService;
	private final Rq rq;


	@PreAuthorize("isAuthenticated()")
	@PostMapping("/addItem/{productId}")
	public String addItem(@PathVariable Long productId) {
		cartService.addItem(rq.getMember(), new Product(productId));

		return rq.redirectToBackWithMsg("장바구니에 추가되었습니다.");
	}


}
