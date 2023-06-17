package com.books.app.rebate.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.books.app.base.dto.RsData;
import com.books.app.base.rq.Rq;
import com.books.app.order.service.OrderService;
import com.books.app.rebate.entity.RebateOrderItem;
import com.books.app.rebate.service.RebateService;
import com.books.util.Ut;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin/rebate")
@RequiredArgsConstructor
@Slf4j
public class AdminRebateController {

	private final RebateService rebateService;
	private final OrderService orderService;


	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/makeData")
	public String showMakeData(Model model) {
		List<String> paidDate = orderService.findByDateFormat_PayDate();

		model.addAttribute("paidDate", paidDate);

		return "admin/rebate/makeData";
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/makeData")
	public String makeDate(String yearMonth) {
		RsData makeDataRsData = rebateService.makeData(yearMonth);

		return Rq.redirectWithMsg("/admin/rebate/rebateOrderItemList?yearMonth=%s".formatted(yearMonth), makeDataRsData);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/rebateOrderItemList")
	public String showRebateOrderItemList(String yearMonth, Model model) {
		List<String> paidDate = orderService.findByDateFormat_PayDate();

		if (!StringUtils.hasText(yearMonth)) {
			yearMonth = paidDate.get(0);
		}

		List<RebateOrderItem> items = rebateService.findRebateOrderItemsByPayDateIn(yearMonth);

		model.addAttribute("paidDate", paidDate);
		model.addAttribute("yearMonth", yearMonth);
		model.addAttribute("items", items);

		return "admin/rebate/rebateOrderItemList";
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/rebateOne/{orderItemId}")
	public String rebateOne(@PathVariable Long orderItemId, HttpServletRequest request) {
		RsData rebateRsData = rebateService.rebate(orderItemId);

		String referer = request.getHeader("Referer");
		String yearMonth = Ut.url.getQueryParamValue(referer, "yearMonth", "");

		return Rq.redirectWithMsg("/admin/rebate/rebateOrderItemList?yearMonth=%s".formatted(yearMonth), rebateRsData);
	}

}
