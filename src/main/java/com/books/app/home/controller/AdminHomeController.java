package com.books.app.home.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping
	public String showIndex() {
		return "redirect:/admin/home/main";
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/home/main")
	public String showMain() {
		return "admin/home/main";
	}

}
