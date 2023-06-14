package com.books.app.rebate.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.books.app.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin/rebate")
@RequiredArgsConstructor
@Slf4j
public class AdminRebateController {

	private final OrderService orderService;


	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/makeData")
	public String showMakeData(Model model) {
		List<String> paidDate = orderService.findByDateFormat_PayDate();

		model.addAttribute("paidDate", paidDate);

		return "admin/rebate/makeData";
	}

}
