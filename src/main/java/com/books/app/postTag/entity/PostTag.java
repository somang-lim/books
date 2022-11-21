package com.books.app.postTag.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.books.app.base.entity.BaseEntity;
import com.books.app.member.entity.Member;
import com.books.app.post.entity.Post;
import com.books.app.postKeyword.entity.PostKeyword;

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
public class PostTag extends BaseEntity {
	@ManyToOne
	@ToString.Exclude
	@OnDelete(action = OnDeleteAction.CASCADE) // 연관 관계에 있는 Entity 를 연쇄적으로 제거 (DDL 에 의해 DB 에서 처리)
	private Post post;

	@ManyToOne
	@ToString.Exclude
	private Member member;

	@ManyToOne
	@ToString.Exclude
	private PostKeyword postKeyword;
}
