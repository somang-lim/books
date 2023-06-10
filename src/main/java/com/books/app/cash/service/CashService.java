package com.books.app.cash.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.cash.entity.CashLog;
import com.books.app.cash.repository.CashLongRepository;
import com.books.app.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashService {

	private final CashLongRepository cashLongRepository;


	@Transactional
	public CashLog addCash(Member member, Long price, String eventType) {
		CashLog cashLog = CashLog
				.builder()
				.member(member)
				.price(price)
				.eventType(eventType)
				.build();

		cashLongRepository.save(cashLog);

		return cashLog;
	}

}
