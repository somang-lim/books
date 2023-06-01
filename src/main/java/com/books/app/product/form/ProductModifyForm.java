package com.books.app.product.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ProductModifyForm {

	@NotBlank
	private String subject;

	@NotNull
	private int price;

	@NotBlank
	private String productTagContents;

}
