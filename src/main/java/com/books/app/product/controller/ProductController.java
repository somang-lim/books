package com.books.app.product.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.books.app.base.exception.ActorCanNotModifyException;
import com.books.app.base.exception.ActorCanNotRemoveException;
import com.books.app.base.rq.Rq;
import com.books.app.member.entity.Member;
import com.books.app.post.entity.Post;
import com.books.app.postKeyword.entity.PostKeyword;
import com.books.app.postKeyword.service.PostKeywordService;
import com.books.app.product.entity.Product;
import com.books.app.product.form.ProductForm;
import com.books.app.product.form.ProductModifyForm;
import com.books.app.product.service.ProductService;
import com.books.app.productTag.entity.ProductTag;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

	private final ProductService productService;
	private final PostKeywordService postKeywordService;
	private final Rq rq;

	@PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
	@GetMapping("/create")
	public String showCreate(Model model) {
		List<PostKeyword> postKeywords = postKeywordService.findByMemberId(rq.getId());

		model.addAttribute("postKeywords", postKeywords);

		return "product/create";
	}

	@PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
	@PostMapping("/create")
	public String create(@Valid ProductForm productForm) {
		Member author = rq.getMember();
		Product product = productService.create(author, productForm);

		return "redirect:/product/" + product.getId();
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		Product product = productService.findForPrintById(id).get();
		List<Post> posts = productService.findPostsByProduct(product);

		model.addAttribute("product", product);
		model.addAttribute("posts", posts);

		return "product/detail";
	}

	@GetMapping("/list")
	public String list(Model model) {
		List<Product> products = productService.findAllForPrintByOrderByIdDesc(rq.getMember());

		model.addAttribute("products", products);

		return "product/list";
	}

	@PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
	@GetMapping("/{id}/modify")
	public String showModify(@PathVariable Long id, Model model) {
		Product product = productService.findForPrintById(id).get();

		Member actor = rq.getMember();

		if (!productService.actorCanModify(actor, product)) {
			throw new ActorCanNotModifyException();
		}

		model.addAttribute("product", product);

		return "product/modify";
	}

	@PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
	@PostMapping("/{id}/modify")
	public String modify(@Valid ProductModifyForm productForm, @PathVariable Long id) {
		Product product = productService.findById(id).get();
		Member actor = rq.getMember();

		if (!productService.actorCanModify(actor, product)) {
			throw new ActorCanNotModifyException();
		}

		productService.modify(product, productForm);

		return Rq.redirectWithMsg("/product/" + product.getId(), "%d번 도서 상품이 수정되었습니다.".formatted(product.getId()));
	}

	@PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
	@PostMapping("/{id}/remove")
	public String remove(@PathVariable Long id) {
		Product product = productService.detail(id);
		Member actor = rq.getMember();

		if (!productService.actorCanRemove(actor, product)) {
			throw new ActorCanNotRemoveException();
		}

		productService.remove(product);

		return Rq.redirectWithMsg("/product/list", "%d번 도서 상품이 삭제되었습니다.".formatted(product.getId()));
	}

	@GetMapping("/tag/{tagContent}")
	public String tagList(@PathVariable String tagContent, Model model) {
		List<ProductTag> productTags = productService.getProductTags(tagContent, rq.getMember());

		model.addAttribute("productTags", productTags);

		return "product/tagList";
	}

}
