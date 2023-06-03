package com.books.app.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.cart.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	Optional<CartItem> findByBuyerIdAndProductId(Long buyerId, Long productId);

	List<CartItem> findAllByBuyerId(Long buyerId);

	List<CartItem> findAllByBuyerIdAndProductIdIn(Long buyerId, long[] productIds);

}
