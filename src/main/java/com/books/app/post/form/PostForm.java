package com.books.app.post.form;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostForm {
	@NotBlank
	private String subject;

	@NotBlank
	private String content;

	@NotBlank
	private String contentHtml;

	@NotBlank
	private String postTagContents;
}
