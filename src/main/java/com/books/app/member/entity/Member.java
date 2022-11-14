package com.books.app.member.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

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

}
