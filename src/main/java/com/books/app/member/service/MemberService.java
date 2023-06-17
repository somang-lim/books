package com.books.app.member.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.AppConfig;
import com.books.app.base.dto.RsData;
import com.books.app.cash.entity.CashLog;
import com.books.app.cash.service.CashService;
import com.books.app.email.service.EmailService;
import com.books.app.emailVerification.service.EmailVerificationService;
import com.books.app.member.entity.AuthLevel;
import com.books.app.member.entity.Member;
import com.books.app.member.exception.AlreadyJoinException;
import com.books.app.member.form.JoinForm;
import com.books.app.member.repository.MemberRepository;
import com.books.app.security.dto.MemberContext;
import com.books.util.Ut;

import lombok.AllArgsConstructor;
import lombok.Data;
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
	private final CashService cashService;


	// 회원가입 로직
	@Transactional
	public Member join(JoinForm joinForm) {
		if (findByUsername(joinForm.getUsername()).isPresent()) {
			throw new AlreadyJoinException();
		}

		AuthLevel authLevel = AuthLevel.USER; // default USER 회원

		// username 이 admin 인 회원은 관리자 회원으로 설정
		if (joinForm.getUsername().equals("admin")) {
			authLevel = AuthLevel.ADMIN;
		}

		// 기본 권한 = 일반
		Member member = Member
			.builder()
			.username(joinForm.getUsername())
			.password(passwordEncoder.encode(joinForm.getPassword()))
			.email(joinForm.getEmail())
			.authLevel(authLevel)
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

		if (!verifyVerificationCodeRs.isSuccess()) {
			return verifyVerificationCodeRs;
		}

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

	public Optional<Member> findById(Long id) {
		return memberRepository.findById(id);
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

	// 임시 비밀번호 설정 로직
	@Transactional
	public void setTempPassword(Member member, String tempPassword) {
		member.setPassword(passwordEncoder.encode(tempPassword));
	}

	// 비밀번호 변경 로직
	@Transactional
	public RsData modifyPassword(Member member, String password, String oldPassword) {
		Member _member = memberRepository.findById(member.getId()).orElse(null);

		if (!passwordEncoder.matches(oldPassword, _member.getPassword())) {
			return RsData.failOf("기존 비밀번호가 일치하지 않습니다.");
		}

		_member.setPassword(passwordEncoder.encode(password));

		return RsData.successOf("비밀번호가 변경되었습니다.");
	}

	// 작가 등록 로직
	@Transactional
	public RsData beAuthor(Member member, String nickname) {
		Optional<Member> _member = memberRepository.findByNickname(nickname);

		if (_member.isPresent()) {
			return RsData.of("F-1", "해당 필명은 이미 사용 중입니다.");
		}

		member.setNickname(nickname);
		memberRepository.save(member);

		forceAuthentication(member);

		return RsData.of("S-1", "해당 필명으로 활동을 시작합니다.");
	}

	// 닉네임 및 예치금 변경으로 Authentication 변경 추가
	private void forceAuthentication(Member member) {
		MemberContext memberContext = new MemberContext(member, member.genAuthorities());

		UsernamePasswordAuthenticationToken authentication =
			UsernamePasswordAuthenticationToken.authenticated(
				memberContext,
				member.getPassword(),
				memberContext.getAuthorities()
			);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);

		SecurityContextHolder.setContext(context);
	}

	public Long getRestCash(Member member) {
		return memberRepository.findById(member.getId()).get().getRestCash();
	}

	@Transactional
	public RsData<AddCashRsDataBody> addCash(Member member, long price) {
		return addCash(member, price, "충전__계좌이체");
	}

	@Transactional
	public RsData<AddCashRsDataBody> addCash(Member member, long price, String eventType) {
		CashLog cashLog = cashService.addCash(member, price, eventType);

		long newRestCash = member.getRestCash() + cashLog.getPrice();
		member.setRestCash(newRestCash);

		memberRepository.save(member);

		forceAuthentication(member);

		return RsData.of(
				"S-1",
				"성공",
				new AddCashRsDataBody(cashLog, newRestCash)
		);
	}

	@Transactional
	public RsData<AddCashRsDataBody> addCashNotForceAuthentication(Member member, long price, String eventType) {
		CashLog cashLog = cashService.addCash(member, price, eventType);

		long newRestCash = member.getRestCash() + cashLog.getPrice();
		member.setRestCash(newRestCash);

		memberRepository.save(member);

		return RsData.of(
			"S-1",
			"성공",
			new AddCashRsDataBody(cashLog, newRestCash)
		);
	}

	@Transactional
	public void setRestCash(Member buyer, long restCash) {
		buyer.setRestCash(restCash);

		memberRepository.save(buyer);
	}

	public List<CashLog> restCashLog(Member member) {
		return cashService.getCashLog(member);
	}

	@Data
	@AllArgsConstructor
	public static class AddCashRsDataBody {
		CashLog cashLog;
		Long newRestCash;
	}
}
