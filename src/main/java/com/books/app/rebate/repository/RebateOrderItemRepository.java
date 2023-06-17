package com.books.app.rebate.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.rebate.entity.RebateOrderItem;

public interface RebateOrderItemRepository extends JpaRepository<RebateOrderItem, Long> {

	Optional<RebateOrderItem> findByOrderItemId(Long orderItemId);

	List<RebateOrderItem> findAllByPayDateBetweenOrderByIdAsc(LocalDateTime fromDate, LocalDateTime toDate);

}
