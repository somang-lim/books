package com.books.app.product.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.books.app.base.rq.Rq;
import com.books.app.member.entity.Member;
import com.books.app.postKeyword.entity.PostKeyword;
import com.books.app.postKeyword.service.PostKeywordService;
import com.books.app.product.entity.Product;
import com.books.app.product.form.ProductForm;
import com.books.app.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

	private final ProductService productService;
	private final PostKeywordService postKeywordService;
	private final Rq rq;

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String showCreate(Model model) {
		List<PostKeyword> postKeywords = postKeywordService.findByMemberId(rq.getId());

		model.addAttribute("postKeywords", postKeywords);

		return "product/create";
	}

}
