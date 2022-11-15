package com.books.app.member.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import com.books.app.base.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = "password")
public class Member extends BaseEntity {
	@Column(unique = true)
	private String username;

	private String password;

	private String email;

	private boolean emailVerified;

	private String nickname;

	public String getName() {
		if (nickname != null) {
			return nickname;
		}

		return username;
	}

	public Member(long id) {
		super(id);
	}

	public String getJdenticon() {
		return "member__" + getId();
	}

	public List<GrantedAuthority> genAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("MEMBER"));

		// 닉네임을 가지고 있다면 작가의 권한을 가진다.
		if (StringUtils.hasText(nickname)) {
			authorities.add(new SimpleGrantedAuthority("AUTHOR"));
		}

		return authorities;
	}
}
