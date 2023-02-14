package com.books.app.attr.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AttrServiceTests {
	@Autowired
	private AttrService attrService;

	@Test
	@DisplayName("영속변수 저장 및 조회")
	void set() {
		attrService.set("member__1__common__homeTown", "안산");

		String member1HomeTown = attrService.get("member__1__common__homeTown", null);

		assertThat(member1HomeTown).isEqualTo("안산");
	}
}
