package com.books.app.rebate.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.base.dto.RsData;
import com.books.app.order.entity.OrderItem;
import com.books.app.order.service.OrderService;
import com.books.app.rebate.entity.RebateOrderItem;
import com.books.app.rebate.repository.RebateOrderItemRepository;
import com.books.util.Ut;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RebateService {

	private final RebateOrderItemRepository rebateOrderItemRepository;
	private final OrderService orderService;


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
}
