package com.books.app.order.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.books.app.member.service.MemberService;
import com.books.app.order.entity.Order;
import com.books.app.order.exception.ActorCanNotPayOrderException;
import com.books.app.order.exception.ActorCanNotSeeException;
import com.books.app.order.exception.OrderIdNotMatchedException;
import com.books.app.order.exception.OrderNotEnoughRestCashException;
import com.books.app.order.service.OrderService;
import com.books.app.security.dto.MemberContext;
import com.books.util.Ut;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	private final MemberService memberService;
	private final Rq rq;
	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper;


	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String create(@AuthenticationPrincipal MemberContext memberContext) {
		Member buyer = memberContext.getMember();
		Order order = orderService.createFromCart(buyer);

		return rq.redirectWithMsg(
			"/order/%d".formatted(order.getId()),
			"%d번 주문이 생성되었습니다.".formatted(order.getId())
		);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}")
	public String showDetail(@PathVariable Long id, @AuthenticationPrincipal MemberContext memberContext, Model model) {
		Order order = orderService.findForPrintById(id).orElse(null);

		if (order == null) {
			return rq.redirectToBackWithMsg("주문을 찾을 수 없습니다.");
		}

		Member actor = memberContext.getMember();
		Long restCash = memberService.getRestCash(actor);


		if (orderService.actorCanSee(actor, order) == false) {
			throw new ActorCanNotSeeException();
		}

		model.addAttribute("order", order);
		model.addAttribute("actorRestCash", restCash);

		return "order/detail";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/list")
	public String showList(Model model) {
		List<Order> orders = orderService.findAllByBuyerId(rq.getId());

		model.addAttribute("orders", orders);

		return "order/list";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{orderId}/cancel")
	public String cancel(@PathVariable Long orderId) {
		RsData rsData = orderService.cancel(orderId, rq.getMember());

		if (rsData.isFail()) {
			return Rq.redirectWithErrorMsg("/order/%d".formatted(orderId), rsData);
		}
		return Rq.redirectWithMsg("/order/%d".formatted(orderId), rsData);
	}

	@PostConstruct
	private void init() {
		restTemplate.setErrorHandler(new ResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) {
				return false;
			}
			@Override
			public void handleError(ClientHttpResponse response) {
			}
		});
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{id}/payByRestCashOnly")
	public String payByRestCashOnly(@PathVariable Long id, @AuthenticationPrincipal MemberContext memberContext) {
		Order order = orderService.findForPrintById(id).get();

		Member actor = memberContext.getMember();
		long restCash = memberService.getRestCash(actor);

		if (!orderService.actorCanPayment(actor, order)) {
			throw new ActorCanNotPayOrderException();
		}

		RsData rsData = orderService.payByRestCashOnly(order);

		return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("예치금 결제 완료했습니다."));
	}

	@Value("${custom.tossPayments.secretKey}")
	private String SECRET_KEY;

	@RequestMapping("/{id}/success")
	public String confirmPayment(
		@PathVariable Long id,
		@RequestParam String paymentKey,
		@RequestParam String orderId,
		@RequestParam Long amount,
		Model model,
		@AuthenticationPrincipal MemberContext memberContext
	) throws Exception {
		Order order = orderService.findForPrintById(id).get();

		Long orderIdInputed = Long.parseLong(orderId.split("__")[1]);

		if (id != orderIdInputed) {
			throw new OrderIdNotMatchedException();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, String> payloadMap = new HashMap<>();
		payloadMap.put("orderId", orderId);
		payloadMap.put("amount", String.valueOf(amount));

		Member actor = memberContext.getMember();
		long restCash = memberService.getRestCash(actor);
		long payPriceRestCash = order.calculatePayPrice() - amount;

		if (payPriceRestCash > restCash) {
			throw new OrderNotEnoughRestCashException();
		}

		HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

		ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
			"https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			orderService.payByTossPayments(order, payPriceRestCash);

			return Rq.redirectWithMsg(
				"/order/%d".formatted(order.getId()),
				"%d번 주문 결제가 완료되었습니다.".formatted(order.getId())
			);
		} else {
			JsonNode failNode = responseEntity.getBody();
			model.addAttribute("message", failNode.get("message").asText());
			model.addAttribute("code", failNode.get("code").asText());

			return "order/fail";
		}
	}

}
