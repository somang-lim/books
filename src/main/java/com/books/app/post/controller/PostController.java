package com.books.app.post.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

	// 글 작성 폼
	@PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
	@GetMapping("/write")
	public String showWrite() {
		return "post/write";
	}
}
