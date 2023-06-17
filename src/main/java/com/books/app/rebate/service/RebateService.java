package com.books.app.rebate.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.base.dto.RsData;
import com.books.app.cash.entity.CashLog;
import com.books.app.member.service.MemberService;
import com.books.app.order.entity.OrderItem;
import com.books.app.order.service.OrderService;
import com.books.app.rebate.entity.RebateOrderItem;
import com.books.app.rebate.repository.RebateOrderItemRepository;
import com.books.util.Ut;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RebateService {

	private final RebateOrderItemRepository rebateOrderItemRepository;
	private final OrderService orderService;
	private final MemberService memberService;


	@Transactional
	public RsData makeData(String yearMonth) {
		int monthEndDay = Ut.date.getEndDayOf(yearMonth);

		String fromDateStr = yearMonth + "-01 00:00:00.000000";
		String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
		LocalDateTime fromDate = Ut.date.parse(fromDateStr);
		LocalDateTime toDate = Ut.date.parse(toDateStr);

		// 데이터 가져오기
		List<OrderItem> orderItems = orderService.findAllByPayDateBetweenOrderByIdAsc(fromDate, toDate);

		// 변환하기
		List<RebateOrderItem> rebateOrderItems = orderItems
				.stream()
				.map(this::toRebateOrderItem)
				.collect(Collectors.toList());

		// 저장하기
		rebateOrderItems.forEach(this::makeRebateOrderItem);

		return RsData.of("S-1", "정산 데이터가 성공적으로 생성되었습니다.");
	}

	@Transactional
	public void makeRebateOrderItem(RebateOrderItem item) {
		RebateOrderItem oldRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(item.getOrderItem().getId()).orElse(null);

		if (oldRebateOrderItem != null) {
			if (oldRebateOrderItem.isRebateDone()) {
				return;
			}

			rebateOrderItemRepository.delete(oldRebateOrderItem);
		}

		rebateOrderItemRepository.save(item);
	}

	private RebateOrderItem toRebateOrderItem(OrderItem orderItem) {
		return new RebateOrderItem(orderItem);
	}

	public List<RebateOrderItem> findRebateOrderItemsByPayDateIn(String yearMonth) {
		int monthEndDay = Ut.date.getEndDayOf(yearMonth);

		String fromDateStr = yearMonth + "-01 00:00:00.000000";
		String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
		LocalDateTime fromDate = Ut.date.parse(fromDateStr);
		LocalDateTime toDate = Ut.date.parse(toDateStr);

		return rebateOrderItemRepository.findAllByPayDateBetweenOrderByIdAsc(fromDate, toDate);
	}

	@Transactional
	public RsData rebate(long orderItemId) {
		RebateOrderItem rebateOrderItem = rebateOrderItemRepository.findByOrderItemId(orderItemId).get();

		if (!rebateOrderItem.isRebateAvailable()) {
			return RsData.of("F-1", "정산이 불가능합니다.");
		}

		int calculateRebatePrice = rebateOrderItem.calculateRebatePrice();

		CashLog cashLog = memberService.addCashNotForceAuthentication(
				rebateOrderItem.getProduct().getAuthor(),
				calculateRebatePrice,
				"정산__%d__지급__예치금".formatted(rebateOrderItem.getOrderItem().getId())
		).getData().getCashLog();

		rebateOrderItem.setRebateDone(cashLog.getId());

		return RsData.of(
			"S-1",
			"주문품목번호 %d번에 대해서 판매자에게 %s원 정산을 완료했습니다.".formatted(rebateOrderItem.getOrderItem().getId(), calculateRebatePrice),
			Ut.mapOf("cashLogId", cashLog.getId())
		);
	}
}
