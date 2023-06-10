package com.books.app.cash.entity;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.books.app.base.entity.BaseEntity;
import com.books.app.member.entity.Member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class CashLog extends BaseEntity {

	private String relTypeCode;
	private Long relId;

	@ManyToOne(fetch = LAZY)
	private Member member;

	private Long price;
	private String eventType;


	public CashLog(Long id) {
		super(id);
	}

}
