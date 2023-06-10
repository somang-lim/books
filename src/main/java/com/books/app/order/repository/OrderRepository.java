package com.books.app.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findAllByBuyerIdOrderByIdDesc(Long buyerId);

	Optional<Order> findByIdAndOrderItems_productId(Long orderId, Long productId);

	Optional<Order> findByBuyerIdAndOrderItems_productId(Long authorId, Long productId);

}
