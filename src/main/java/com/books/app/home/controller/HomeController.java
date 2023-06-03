package com.books.app.home.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.books.app.base.rq.Rq;
import com.books.app.post.entity.Post;
import com.books.app.post.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final PostService postService;
	private final Rq rq;



	@GetMapping("/")
	public String showMain(Model model) {
		List<Post> posts = postService.list(rq.getId());

		model.addAttribute("posts", posts);

		return "home/main";
	}
}
