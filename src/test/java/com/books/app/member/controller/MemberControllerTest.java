package com.books.app.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.member.service.MemberService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MemberControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private MemberService memberService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("회원가입 폼")
	void showJoin() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/member/join"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is2xxSuccessful())
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("showJoin"))
			.andExpect(content().string(containsString("회원가입")));
	}

	@Test
	@DisplayName("회원가입")
	void join() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/member/join")
					.with(csrf())
					.param("username", "user10")
					.param("password", "1234")
					.param("email", "user10@test.com")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is3xxRedirection())
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("join"))
			.andExpect(redirectedUrlPattern("/member/login?msg=**"));

		assertThat(memberService.findByUsername("user10").isPresent()).isTrue();
	}

	@Test
	@DisplayName("로그인 폼")
	void showLogin() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/member/login"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is2xxSuccessful())
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("showLogin"))
			.andExpect(content().string(containsString("로그인")));
	}

	@Test
	@DisplayName("아이디찾기 폼")
	void showFindUsername() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/member/findUsername"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is2xxSuccessful())
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("showFindUsername"))
			.andExpect(content().string(containsString("아이디 찾기")));
	}

	@Test
	@DisplayName("아이디찾기")
	void findUsername() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/member/findUsername")
				.with(csrf())
				.param("email", "user1@test.com")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is3xxRedirection())
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("findUsername"))
			.andExpect(redirectedUrlPattern("/member/login?username=**"));
	}

	@Test
	@DisplayName("비밀번호찾기 폼")
	void showFindPassword() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/member/findPassword"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is2xxSuccessful())
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("showFindPassword"))
			.andExpect(content().string(containsString("비밀번호 찾기")));
	}

	@Test
	@DisplayName("비밀번호찾기")
	void findPassword() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/member/findPassword")
				.with(csrf())
				.param("username", "user1")
				.param("email", "user1@test.com")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is3xxRedirection())
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("findPassword"))
			.andExpect(redirectedUrlPattern("/member/login?username=**"));
	}

	@Test
	@DisplayName("회원정보 조회")
	@WithUserDetails("user1")
	void profile() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/member/profile"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is2xxSuccessful())
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("profile"))
			.andExpect(content().string(containsString("프로파일")));
	}

	@Test
	@DisplayName("비밀번호 변경 폼")
	@WithUserDetails("user1")
	void showModifyPassword() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/member/modifyPassword"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is2xxSuccessful())
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("showModifyPassword"))
			.andExpect(content().string(containsString("비밀번호 변경")));
	}

	@Test
	@DisplayName("비밀번호 변경")
	@WithUserDetails("user1")
	void modifyPassword() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/member/modifyPassword")
				.with(csrf())
				.param("oldPassword", "1234")
				.param("password", "12341234")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is3xxRedirection())
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("modifyPassword"))
			.andExpect(redirectedUrlPattern("/?msg=**"));

		assertThat(passwordEncoder.matches("12341234", memberService.findByUsername("user1").get().getPassword())).isTrue();
	}

	@Test
	@DisplayName("작가 활동 시작 폼")
	@WithUserDetails("user1")
	void showBeAuthor() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/member/beAuthor"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is2xxSuccessful())
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("showBeAuthor"))
			.andExpect(content().string(containsString("작가 시작")));
	}

	@Test
	@DisplayName("작가 활동 시작")
	@WithUserDetails("user1")
	void beAuthor() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/member/beAuthor")
				.with(csrf())
				.param("nickname", "소망")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is3xxRedirection())
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("beAuthor"))
			.andExpect(redirectedUrlPattern("/?msg=**"));

		assertThat(memberService.findByUsername("user1").get().getNickname()).isEqualTo("소망");
	}
}
