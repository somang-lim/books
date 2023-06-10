package com.books.app.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.books.util.Ut;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
		String uri = request.getRequestURI();

		if (uri.equals("/post/write")) {
			response.sendRedirect("/member/beAuthor?msg=" + Ut.url.encode("먼저 필명을 작성해주세요."));
			return;
		}

		if (uri.equals("/product/create")) {
			response.sendRedirect("/?msg=" + Ut.url.encode("작성된 글이 있어야 도서를 등록할 수 있습니다."));
			return;
		}


		response.sendRedirect("/");
	}

}
