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
		String subject = "[%s 이메일인증] 안녕하세요 %s님".formatted(AppConfig.getSiteName(), member.getName());
		String url = genEmailVerificationUrl(member);

		RsData<Long> sendEmailRs = emailService.sendEmail(email, subject, url);

		return sendEmailRs;
	}

	public String genEmailVerificationUrl(Member member) {
		return genEmailVerificationUrl(member.getId());
	}

	public String genEmailVerificationUrl(Long memberId) {
		String code = genEmailVerificationCode(memberId);

		String body = "<h2>아래 내용을 클릭하여 회원가입을 완료하세요.</h2>";
		body += "<a href=\"%s/emailVerification/verify?memberId=%d&code=%s\" target=\"_blank\">이메일 인증하기</a>".formatted(AppConfig.getSiteBaseUrl(), memberId, code);
		return body;
	}

	public String genEmailVerificationCode(Long memberId) {
		String code = UUID.randomUUID().toString();
		attrService.set("member__%d__extra__emailVerificationCode".formatted(memberId), code, LocalDateTime.now().plusSeconds(60 * 60 * 1));

		return code;
	}

	public RsData verifyVerificationCode(Long memberId, String code) {
		String foundCode = attrService.get("member__%d__extra__emailVerificationCode".formatted(memberId), "");

		if (!foundCode.equals(code)) {
			return RsData.of("F-1", "만료되었거나 유효하지 않은 코드입니다.");
		}

		return RsData.of("S-1", "인증된 코드입니다.");
	}
}
