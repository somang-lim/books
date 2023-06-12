package com.books.app.cash.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.cash.entity.CashLog;

public interface CashLogRepository extends JpaRepository<CashLog, Long> {

	List<CashLog> findByMemberId(Long memberId);

}
