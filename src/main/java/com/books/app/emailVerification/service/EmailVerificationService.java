package com.books.app.emailVerification.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.AppConfig;
import com.books.app.attr.service.AttrService;
import com.books.app.base.dto.RsData;
import com.books.app.email.service.EmailService;
import com.books.app.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
	private final EmailService emailService;
	private final AttrService attrService;

	public RsData<Long> send(Member member) {
		String email = member.getEmail();
		String subject = "[%s 이메일인증] 안녕하세요 %s님. 링크를 클릭하여 회원가입을 완료하세요.".formatted(AppConfig.getSiteName(), member.getName());
		String url = genEmailVerificationUrl(member);

		RsData<Long> sendEmailRs = emailService.sendEmail(email, subject, url);

		return sendEmailRs;
	}

	public String genEmailVerificationUrl(Member member) {
		return genEmailVerificationUrl(member.getId());
	}

	public String genEmailVerificationUrl(Long memberId) {
		String code = genEmailVerificationCode(memberId);

		return AppConfig.getSiteBaseUrl() + "/emailVerification/verify?memberId=%d&code=%s".formatted(memberId, code);
	}

	public String genEmailVerificationCode(Long memberId) {
		String code = UUID.randomUUID().toString();
		attrService.set("member__%d__extra__emailVerificationCode".formatted(memberId), code, LocalDateTime.now().plusSeconds(60 * 60 * 1));

		return code;
	}
}
