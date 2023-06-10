package com.books.app.member.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;

import org.hibernate.annotations.ColumnDefault;
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

	private long restCash;

	private String nickname;

	@Convert(converter = AuthLevelConverter.class)
	private AuthLevel authLevel;

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

		// 모든 로그인한 회원에게는 USER 권한 부여
		authorities.add(new SimpleGrantedAuthority(AuthLevel.USER.getValue())); // 일반 회원

		// nickname 이 있으면 AUTHOR 권한 부여
		if (StringUtils.hasText(nickname)) {
			authorities.add(new SimpleGrantedAuthority("AUTHOR"));
		}

		// authLevel 이 7이면 ADMIN 권한 부여
		if (this.authLevel == AuthLevel.ADMIN) {
			authorities.add(new SimpleGrantedAuthority(AuthLevel.ADMIN.getValue())); // 관리자 회원
		}

		return authorities;
	}
}
