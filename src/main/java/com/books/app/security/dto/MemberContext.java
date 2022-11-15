package com.books.app.security.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.books.app.member.entity.Member;

import lombok.Getter;

@Getter
public class MemberContext extends User {
	private final Long id;
	private final LocalDateTime createDate;
	private final LocalDateTime updateDate;
	private final String username;
	private final String email;

	public MemberContext(Member member, List<GrantedAuthority> authorities) {
		super(member.getUsername(), member.getPassword(), authorities);

		this.id = member.getId();
		this.createDate = member.getCreateDate();
		this.updateDate = member.getUpdateDate();
		this.username = member.getUsername();
		this.email = member.getEmail();
	}

	public Member getMember() {
		return Member
			.builder()
			.id(id)
			.createDate(createDate)
			.updateDate(updateDate)
			.username(username)
			.email(email)
			.build();
	}

	public boolean hasAuthority(String authorityName) {
		return getAuthorities()
			.stream()
			.anyMatch(
				grantedAuthority -> grantedAuthority
					.getAuthority().equals(authorityName)
			);
	}

	public String getName() {
		return getMember().getName();
	}
}
