package com.books.app.member.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.books.app.base.rq.Rq;
import com.books.app.member.form.JoinForm;
import com.books.app.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
	private final MemberService memberService;

	@PreAuthorize("isAnonymous()")
	@GetMapping("/join")
	public String showJoin() {
		return "member/join";
	}

	@PreAuthorize("isAnonymous()")
	@PostMapping("/join")
	public String join(@Validated JoinForm joinForm) {
		memberService.join(joinForm);

		return Rq.redirectWithMsg("/member/login", "회원가입이 완료되었습니다.");
	}

	@PreAuthorize("isAnonymous()")
	@GetMapping("/login")
	public String showLogin(HttpServletRequest request) {
		String uri = request.getHeader("Referer");

		if (uri != null && !uri.contains("/member/login")) {
			request.getSession().setAttribute("prevPage", uri);
		}

		return "member/login";
	}
}
