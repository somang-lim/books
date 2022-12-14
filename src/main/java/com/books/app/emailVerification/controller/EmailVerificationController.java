package com.books.app.emailVerification.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.books.app.base.dto.RsData;
import com.books.app.base.rq.Rq;
import com.books.app.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/emailVerification")
public class EmailVerificationController {
	private final MemberService memberService;
	private final Rq rq;

	@GetMapping("/verify")
	public String verify(long memberId, String code) {
		RsData verifyEmailRsData = memberService.verifyEmail(memberId, code);

		if (verifyEmailRsData.isFail()) {
			return Rq.redirectWithMsg("/", verifyEmailRsData);
		}

		String successMsg = verifyEmailRsData.getMsg();

		memberService.sendWelcome(memberId);

		if (!rq.isLogin()) {
			return Rq.redirectWithMsg("/member/login", successMsg);
		}

		return Rq.redirectWithMsg("/", successMsg);
	}
}
