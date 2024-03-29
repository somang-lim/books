package com.books.app.home.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class HomeControllerTests {

	@Autowired
	private MockMvc mvc;

	@Test
	@DisplayName("메인 화면")
	void showMain() throws Exception {
		// WHEN

		// GET /
		ResultActions resultActions = mvc
			.perform(get("/"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is2xxSuccessful())
			.andExpect(handler().handlerType(HomeController.class))
			.andExpect(handler().methodName("showMain"))
			.andExpect(content().string(containsString("메인")));
	}

}
