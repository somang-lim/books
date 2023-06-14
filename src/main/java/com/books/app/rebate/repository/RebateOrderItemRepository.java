package com.books.app.rebate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.rebate.entity.RebateOrderItem;

public interface RebateOrderItemRepository extends JpaRepository<RebateOrderItem, Long> {

	Optional<RebateOrderItem> findByOrderItemId(Long orderItemId);

}
