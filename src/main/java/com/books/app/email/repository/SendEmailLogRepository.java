package com.books.app.email.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.email.entity.SendEmailLog;

public interface SendEmailLogRepository extends JpaRepository<SendEmailLog, Long> {
}
