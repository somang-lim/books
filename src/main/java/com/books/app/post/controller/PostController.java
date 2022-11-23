package com.books.app.post.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.books.app.base.rq.Rq;
import com.books.app.member.entity.Member;
import com.books.app.post.entity.Post;
import com.books.app.post.form.PostForm;
import com.books.app.post.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;
	private final Rq rq;

	// 글 작성 폼
	@PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
	@GetMapping("/write")
	public String showWrite() {
		return "post/write";
	}

	// 글 작성 로직
	@PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
	@PostMapping("/write")
	public String write(@Validated PostForm postForm) {
		Member author = rq.getMember();

		Post post = postService.write(author, postForm);

		return Rq.redirectWithMsg("/post/" + post.getId(), "%d번 글이 생성되었습니다.".formatted(post.getId()));
	}

	// 글 조회 폼
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		Post post = postService.detail(id);

		Member actor = rq.getMember();

		model.addAttribute("post", post);

		return "post/detail";
	}
}
