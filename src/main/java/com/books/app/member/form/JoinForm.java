package com.books.app.member.form;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinForm {
	@NotEmpty
	private String username;

	@NotEmpty
	private String password;

	@NotEmpty
	private String email;

	private String nickname;
}
