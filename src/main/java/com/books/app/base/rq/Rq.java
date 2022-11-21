package com.books.app.base.rq;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.books.app.base.dto.RsData;
import com.books.app.member.entity.Member;
import com.books.app.security.dto.MemberContext;
import com.books.util.Ut;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequestScope
public class Rq {
	private final HttpServletRequest req;
	private final HttpServletResponse resp;
	private final MemberContext memberContext;

	@Getter
	private final Member member;

	public Rq(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;

		// 현재 로그인한 회원의 인증정보를 가지고 온다.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication.getPrincipal() instanceof MemberContext) {
			this.memberContext = (MemberContext) authentication.getPrincipal();
			this.member = memberContext.getMember();
		} else {
			this.memberContext = null;
			this.member = null;
		}
	}

	public boolean hasAuthority(String authorityName) {
		if (memberContext == null)
			return false;

		return memberContext.hasAuthority(authorityName);
	}

	public String historyBack(String msg) {
		req.setAttribute("alertMsg", msg);

		return "common/js";
	}

	public String historyBack(RsData rsData) {
		return historyBack(rsData.getMsg());
	}

	public String redirectToBackWithMsg(String msg) {
		String url = req.getHeader("Referer");

		return redirectWithMsg(url, msg);
	}

	public static String redirectWithMsg(String url, String msg) {
		return "redirect:" + urlWithMsg(url, msg);
	}

	public static String redirectWithMsg(String url, RsData rsData) {
		return "redirect:" + urlWithMsg(url, rsData);
	}

	public static String redirectWithErrorMsg(String url, RsData rsData) {
		url = Ut.url.modifyQueryParam(url, "errorMsg", msgWithTtl(rsData.getMsg()));

		return "redirect:" + url;
	}

	public static String urlWithMsg(String url, String msg) {
		return Ut.url.modifyQueryParam(url, "msg", msgWithTtl(msg));
	}

	public static String urlWithMsg(String url, RsData rsData) {
		if (rsData.isFail()) {
			return urlWithErrorMsg(url, rsData.getMsg());
		}

		return urlWithMsg(url, rsData.getMsg());
	}

	public static String urlWithErrorMsg(String url, String errorMsg) {
		return Ut.url.modifyQueryParam(url, "errorMsg", msgWithTtl(errorMsg));
	}

	public static String msgWithTtl(String msg) {
		return Ut.url.encode(msg) + ";ttl=" + new Date().getTime();
	}

	public long getId() {
		if (isLogout()) {
			return 0;
		}

		return getMember().getId();
	}

	public boolean isLogout() {
		return member == null;
	}

	public boolean isLogin() {
		return isLogout() == false;
	}
}
