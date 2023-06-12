package com.books.app.cash.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.cash.entity.CashLog;
import com.books.app.cash.repository.CashLogRepository;
import com.books.app.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashService {

	private final CashLogRepository cashLogRepository;


	@Transactional
	public CashLog addCash(Member member, Long price, String eventType) {
		CashLog cashLog = CashLog
				.builder()
				.member(member)
				.price(price)
				.eventType(eventType)
				.build();

		cashLogRepository.save(cashLog);

		return cashLog;
	}

	public List<CashLog> getCashLog(Member member) {
		return cashLogRepository.findByMemberId(member.getId());
	}

}
