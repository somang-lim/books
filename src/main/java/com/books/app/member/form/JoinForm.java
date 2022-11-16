package com.books.app.member.form;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JoinForm {
	@NotEmpty
	private String username;

	@NotEmpty
	private String password;

	@NotEmpty
	private String email;
}
