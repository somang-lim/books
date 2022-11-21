package com.books.app.security.service;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.books.app.member.entity.Member;
import com.books.app.member.repository.MemberRepository;
import com.books.app.security.dto.MemberContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.findByUsername(username).orElse(null);

		if (!member.isEmailVerified()) {
			throw new LockedException("이메일이 인증되지 않았습니다.");
		}

		return new MemberContext(member, member.genAuthorities());
	}
}
