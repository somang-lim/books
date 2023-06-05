package com.books.app.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
