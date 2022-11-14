package com.books.app.member.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


		log.debug("member: " + member);

		memberRepository.save(member);

		return member;
	}

	public Optional<Member> findByUsername(String username) {
		return memberRepository.findByUsername(username);
	}
}
