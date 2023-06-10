package com.books.app.cash.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.cash.entity.CashLog;

public interface CashLongRepository extends JpaRepository<CashLog, Long> {
}
