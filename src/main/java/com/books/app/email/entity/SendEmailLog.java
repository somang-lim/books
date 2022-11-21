package com.books.app.email.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;

import com.books.app.base.entity.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class SendEmailLog extends BaseEntity {
	private String resultCode;

	private String message;

	private String email;

	private String subject;

	private String body;

	private LocalDateTime sendEndDate;

	private LocalDateTime failDate;
}
