package com.books.app.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.books.app.base.rq.Rq;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
										AuthenticationException exception) throws IOException {
		String url = Rq.urlWithMsg("/member/login", "로그인 정보가 올바르지 않습니다.");

		redirectStrategy.sendRedirect(request, response, url);
	}
}
