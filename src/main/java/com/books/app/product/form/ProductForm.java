package com.books.app.product.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ProductForm {

	@NotBlank
	private String subject;

	@NotNull
	private int price;

	@NotNull
	private Long postKeywordId;

	@NotBlank
	private String productTagContents;

}
