package com.books.app.base.entity;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder // 상속되는 클래스의 Builder 를 생성
@MappedSuperclass // 공통 매핑 정보가 필요할 때 부모 클래스에 정의하고 상속받아 해당 필드를 사용하여 중복을 제거
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 해당 클래스에 Auditing 기능을 포함 (자동으로 값 입력)
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BaseEntity {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@CreatedDate
	private LocalDateTime createDate;

	@LastModifiedDate
	private LocalDateTime updateDate;

	@Transient // 아래 필드가 DB 필드가 되는 것을 막는다.
	@Builder.Default
	// 이 필드 덕분에 다양한 DTO 클래스를 만들 필요성이 줄어들게 된다.
	// 하지만 이 방식은 DTO 방식에 비해서 휴먼 에러가 일어날 확률이 높다. → TDD 를 통한 보완 필요
	private Map<String, Object> extra = new LinkedHashMap<>();

	public BaseEntity(long id) {
		this.id = id;
	}
}
