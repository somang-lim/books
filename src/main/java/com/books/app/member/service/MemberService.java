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
import com.books.util.Ut;

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
			.build();

		memberRepository.save(member);

		emailVerificationService.send(member);

		return member;
	}

	// 테스트 데이터 강제 이메일 인증 처리 로직
	@Transactional
	public void forceEmailVerify(Member member) {
		member.setEmailVerified(true);

		memberRepository.save(member);
	}


	public Optional<Member> findByUsername(String username) {
		return memberRepository.findByUsername(username);
	}

	// 회원가입 이메일 인증 로직
	@Transactional
	public RsData verifyEmail(Long id, String verificationCode) {
		RsData verifyVerificationCodeRs = emailVerificationService.verifyVerificationCode(id, verificationCode);

		if (!verifyVerificationCodeRs.isSuccess())
			return verifyVerificationCodeRs;

		Member member = memberRepository.findById(id).orElse(null);
		member.setEmailVerified(true);

		return RsData.of("S-1", "이메일 인증이 완료되었습니다.");
	}

	// 회원가입 축하 이메일 발송 로직
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

	public Member findByUsernameAndEmail(String username, String email) {
		return memberRepository.findByUsernameAndEmail(username, email).orElse(null);
	}

	// 임시비밀번호 발송 로직
	@Transactional
	public RsData sendTempPasswordToEmail(Member member) {
		String subject = "[%s] 임시 비밀번호 발송".formatted(AppConfig.getSiteName());
		String tempPassword = Ut.getTempPassword(6);
		String body = "<h2>임시 비밀번호: %s</h2>".formatted(tempPassword);
		body += "<a href=\"%s/member/login\" target=\"_blank\">로그인 하러가기</a>".formatted(AppConfig.getSiteBaseUrl());

		RsData sendResultData = emailService.sendEmail(member.getEmail(), subject, body);

		if (sendResultData.isFail()) {
			return sendResultData;
		}

		setTempPassword(member, tempPassword);

		return RsData.of("S-1", "계정의 이메일주소로 임시 비밀번호가 발송되었습니다.");
	}

	@Transactional
	public void setTempPassword(Member member, String tempPassword) {
		member.setPassword(passwordEncoder.encode(tempPassword));
	}
}
