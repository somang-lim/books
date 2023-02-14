package com.books.app.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.base.dto.RsData;
import com.books.app.member.entity.Member;
import com.books.app.member.form.JoinForm;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberServiceTests {

	@Autowired
	private MemberService memberService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("회원가입")
	void join() {
		JoinForm joinForm = new JoinForm("user10", "1234", "user10@test.com");

		memberService.join(joinForm);

		Member foundMember = memberService.findByUsername("user10").get();

		assertThat(foundMember.getCreateDate()).isNotNull();
		assertThat(foundMember.getUsername()).isNotNull();
		assertThat(passwordEncoder.matches("1234", foundMember.getPassword())).isTrue();
	}

	@Test
	@DisplayName("임시 비밀번호 설정")
	@WithUserDetails("user1")
	void setTempPassword() {
		Member member = memberService.findByUsername("user1").get();

		memberService.setTempPassword(member, "12341234");

		Member foundMember = memberService.findByUsername("user1").get();

		assertThat(foundMember.getUpdateDate()).isNotNull();
		assertThat(passwordEncoder.matches("12341234", foundMember.getPassword())).isTrue();
	}

	@Test
	@DisplayName("비밀번호 변경")
	@WithUserDetails("user1")
	void modifyPassword() {
		Member member = memberService.findByUsername("user1").get();

		RsData rsData = memberService.modifyPassword(member, "12341234", "1234");

		Member foundMember = memberService.findByUsername("user1").get();

		assertThat(foundMember.getUpdateDate()).isNotNull();
		assertThat(passwordEncoder.matches("12341234", foundMember.getPassword())).isTrue();
		assertThat(rsData.getResultCode()).isEqualTo("S-1");
		assertThat(rsData.getData()).isEqualTo("비밀번호가 변경되었습니다.");
	}

	@Test
	@DisplayName("작가 등록")
	@WithUserDetails("user1")
	void beAuthor() {
		Member member = memberService.findByUsername("user1").get();

		RsData rsData = memberService.beAuthor(member, "소망");

		Member foundMember = memberService.findByUsername("user1").get();

		assertThat(foundMember.getUpdateDate()).isNotNull();
		assertThat(foundMember.getNickname()).isEqualTo("소망");
		assertThat(rsData.getResultCode()).isEqualTo("S-1");
		assertThat(rsData.getMsg()).isEqualTo("해당 필명으로 활동을 시작합니다.");
	}
}
