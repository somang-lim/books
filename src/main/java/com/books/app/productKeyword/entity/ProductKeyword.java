package com.books.app.productKeyword.entity;

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
@ToString(callSuper = true)
public class ProductKeyword extends BaseEntity {

	private String content;

	public Object getListUrl() {
		return "/product/tag/" + content;
	}

}
