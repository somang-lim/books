package com.books.app.member.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.books.app.base.dto.RsData;
import com.books.app.base.rq.Rq;
import com.books.app.member.entity.Member;
import com.books.app.member.form.JoinForm;
import com.books.app.member.service.MemberService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {
	private final MemberService memberService;
	private final Rq rq;
	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper;


	// 회원가입 폼
	@PreAuthorize("isAnonymous()")
	@GetMapping("/join")
	public String showJoin() {
		return "member/join";
	}

	// 회원가입 로직
	@PreAuthorize("isAnonymous()")
	@PostMapping("/join")
	public String join(@Validated JoinForm joinForm) {
		memberService.join(joinForm);

		return Rq.redirectWithMsg("/member/login", "회원가입이 완료되었습니다.");
	}

	// 로그인 폼
	@PreAuthorize("isAnonymous()")
	@GetMapping("/login")
	public String showLogin(HttpServletRequest request) {
		String uri = request.getHeader("Referer");

		if (uri != null && !uri.contains("/member/login")) {
			request.getSession().setAttribute("prevPage", uri);
		}

		return "member/login";
	}

	// 아이디찾기 폼
	@PreAuthorize("isAnonymous()")
	@GetMapping("/findUsername")
	public String showFindUsername() {
		return "member/findUsername";
	}

	// 아이디찾기 로직
	@PreAuthorize("isAnonymous()")
	@PostMapping("/findUsername")
	public String findUsername(String email) {
		Member member = memberService.findByEmail(email);

		if (member == null) {
			return rq.historyBack("일치하는 회원이 존재하지 않습니다.");
		}

		return Rq.redirectWithMsg("/member/login?username=%s".formatted(member.getUsername()), "해당 이메일로 가입한 계정의 아이디는 '%s' 입니다.".formatted(member.getUsername()));
	}

	// 비밀번호찾기 폼
	@PreAuthorize("isAnonymous()")
	@GetMapping("/findPassword")
	public String showFindPassword() {
		return "member/findPassword";
	}


	// 비밀번호찾기 로직
	@PreAuthorize("isAnonymous()")
	@PostMapping("/findPassword")
	public String findPassword(String username, String email) {
		Member member = memberService.findByUsernameAndEmail(username, email);

		if (member == null) {
			return rq.historyBack("일치하는 회원이 존재하지 않습니다.");
		}

		RsData sendTempLoginPwToEmailResultData = memberService.sendTempPasswordToEmail(member);

		if (sendTempLoginPwToEmailResultData.isFail()) {
			return rq.historyBack(sendTempLoginPwToEmailResultData);
		}

		return Rq.redirectWithMsg("/member/login?username=%s".formatted(member.getUsername()), "해당 이메일로 '%s' 계정의 임시비밀번호를 발송했습니다.".formatted(member.getUsername()));
	}

	// 회원정보 조회
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/profile")
	public String profile() {
		Member member = rq.getMember();

		return "member/profile";
	}

	// 비밀번호 변경 폼
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modifyPassword")
	public String showModifyPassword() {
		return "member/modifyPassword";
	}

	// 비밀번호 변경 로직
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modifyPassword")
	public String modifyPassword(String oldPassword, String password) {
		Member member = rq.getMember();
		RsData rsData = memberService.modifyPassword(member, password, oldPassword);

		if (rsData.isFail()) {
			return rq.historyBack(rsData.getMsg());
		}

		return Rq.redirectWithMsg("/", rsData);
	}

	// 작가 활동 시작 폼
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/beAuthor")
	public String showBeAuthor() {
		return "member/beAuthor";
	}

	// 작가 활동 시작 로직
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/beAuthor")
	public String beAuthor(String nickname) {
		Member member = rq.getMember();

		RsData rsData = memberService.beAuthor(member, nickname);

		if (rsData.isFail()) {
			return Rq.redirectWithMsg("/member/beAuthor", rsData);
		}

		return Rq.redirectWithMsg("/", rsData);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/addRestCash")
	public String addRestCash() {
		return "member/addRestCash";
	}

	@PostConstruct
	private void init() {
		restTemplate.setErrorHandler(new ResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
			}
		});
	}

	@Value("${custom.tossPayments.secretKey}")
	private String SECRET_KEY;

	@RequestMapping("/{id}/success")
	public String addRestCash(
		@PathVariable Long id,
		@RequestParam String paymentKey,
		@RequestParam String orderId,
		@RequestParam Long amount,
		Model model
	) throws Exception {
		Member member = memberService.findById(id).get();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, String> payloadMap = new HashMap<>();
		payloadMap.put("orderId", orderId);
		payloadMap.put("amount", String.valueOf(amount));

		HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

		ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
			"https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			memberService.addCash(member, amount);

			return Rq.redirectWithMsg(
				"/member/profile",
				"예치금 충전이 완료되었습니다."
			);
		} else {
			JsonNode failNode = responseEntity.getBody();
			model.addAttribute("message", failNode.get("message").asText());
			model.addAttribute("code", failNode.get("code").asText());

			return "member/addRestCashFail";
		}

	}

}
