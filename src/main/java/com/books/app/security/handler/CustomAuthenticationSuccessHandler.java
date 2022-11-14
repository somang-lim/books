package com.books.app.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import com.books.app.security.dto.MemberContext;
import com.books.util.Ut;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final RequestCache requestCache = new HttpSessionRequestCache();
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {
		String url = "/";

		clearSession(request);

		SavedRequest savedRequest = requestCache.getRequest(request, response);

		String prevPage = (String) request.getSession().getAttribute("prevPage");
		if (prevPage != null) {
			request.getSession().removeAttribute("prevPage");
		}

		if (savedRequest != null) {
			url = savedRequest.getRedirectUrl();
		} else if (prevPage != null && prevPage.length() > 0) {
			// 회원가입 → 로그인으로 넘어온 경우 "/"로 Redirect
			if (prevPage.contains("/member/login")) {
				url = "/";
			} else {
				url = prevPage;
			}
		}

		MemberContext memberContext = (MemberContext) authentication.getPrincipal();

		url = Ut.url.modifyQueryParam(url, "msg", memberContext.getUsername() + "님 환영합니다.");

		redirectStrategy.sendRedirect(request, response, url);

	}

	// 로그인 실패 시 에러 세션 제거
	protected void clearSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}
