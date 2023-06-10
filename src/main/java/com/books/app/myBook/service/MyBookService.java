package com.books.app.myBook.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.base.dto.RsData;
import com.books.app.myBook.entity.MyBook;
import com.books.app.myBook.repository.MyBookRepository;
import com.books.app.order.entity.Order;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyBookService {

	private final MyBookRepository myBookRepository;


	@Transactional
	public RsData add(Order order) {
		order.getOrderItems()
				.stream()
				.map(orderItem -> MyBook.builder()
					.owner(order.getBuyer())
					.orderItem(orderItem)
					.product(orderItem.getProduct())
					.build())
			.forEach(myBookRepository::save);

		return RsData.of("S-1", "나의 책장에 추가되었습니다.");
	}

	@Transactional
	public void remove(Order order) {
		order.getOrderItems()
				.stream()
				.forEach(orderItem -> myBookRepository.deleteByProductIdAndOwnerId(orderItem.getProduct().getId(), order.getBuyer().getId()));
	}

}
