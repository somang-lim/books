package com.books.app.member.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.AppConfig;
import com.books.app.base.dto.RsData;
import com.books.app.email.service.EmailService;
import com.books.app.emailVerification.service.EmailVerificationService;
import com.books.app.member.entity.Member;
import com.books.app.member.exception.AlreadyJoinException;
import com.books.app.member.form.JoinForm;
import com.books.app.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailVerificationService emailVerificationService;
	private final EmailService emailService;

	// 회원가입 로직
	@Transactional
	public Member join(JoinForm joinForm) {
		if (findByUsername(joinForm.getUsername()).isPresent()) {
			throw new AlreadyJoinException();
		}

		Member member = Member
			.builder()
			.username(joinForm.getUsername())
			.password(passwordEncoder.encode(joinForm.getPassword()))
			.email(joinForm.getEmail())
			.nickname(joinForm.getNickname())
			.build();

		memberRepository.save(member);

		emailVerificationService.send(member);

		return member;
	}

	public Optional<Member> findByUsername(String username) {
		return memberRepository.findByUsername(username);
	}

	// 회원가입 이메일 인증 발송
	@Transactional
	public RsData verifyEmail(Long id, String verificationCode) {
		RsData verifyVerificationCodeRs = emailVerificationService.verifyVerificationCode(id, verificationCode);

		if (!verifyVerificationCodeRs.isSuccess())
			return verifyVerificationCodeRs;

		Member member = memberRepository.findById(id).orElse(null);
		member.setEmailVerified(true);

		return RsData.of("S-1", "이메일 인증이 완료되었습니다.");
	}

	// 회원가입 축하 이메일 발송
	@Transactional
	public RsData sendWelcome(Long id) {
		Member member = memberRepository.findById(id).orElse(null);
		String email = member.getEmail();
		String subject = "[%s] %s님, 회원가입을 축하합니다.".formatted(AppConfig.getSiteName(), member.getName());
		String body = "IMBOOKS에서 회원님의 많은 꿈을 이루시길 바라요 :)";

		return emailService.sendEmail(email, subject, body);
	}

	public Member findByEmail(String email) {
		return memberRepository.findByEmail(email).orElse(null);
	}
}
