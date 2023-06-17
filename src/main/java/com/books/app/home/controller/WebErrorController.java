package com.books.app.home.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.books.app.base.rq.Rq;
import com.books.app.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebErrorController implements ErrorController {

	private final Rq rq;

	@GetMapping("/error")
	public String handleError(HttpServletRequest request) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		Member actor = rq.getMember();
		if (actor.getAuthLevel().getCode() < 7) {

		}

		if (status != null) {
			int statusCode = Integer.valueOf(status.toString());

			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				if (actor.getAuthLevel().getCode() < 7) {
					return "error/404";
				} else {
					return "error/admin/404";
				}
			}
		}

		return "error/error";
	}

}
