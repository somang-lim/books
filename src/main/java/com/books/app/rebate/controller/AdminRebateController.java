package com.books.app.rebate.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.books.app.base.dto.RsData;
import com.books.app.base.rq.Rq;
import com.books.app.order.service.OrderService;
import com.books.app.rebate.service.RebateService;

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
		log.info("yearMonth: " + yearMonth);
		RsData makeDataRsData = rebateService.makeData(yearMonth);

		return Rq.redirectWithMsg("/admin/rebate/rebateOrderItemList?yearMonth=%s".formatted(yearMonth), makeDataRsData);
	}

}
