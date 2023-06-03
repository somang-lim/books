package com.books.app.cart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.cart.entity.CartItem;
import com.books.app.cart.repository.CartItemRepository;
import com.books.app.member.entity.Member;
import com.books.app.product.entity.Product;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

	private final CartItemRepository cartItemRepository;


	@Transactional
	public CartItem addItem(Member buyer, Product product) {
		CartItem oldCartItem = cartItemRepository.findByBuyerIdAndProductId(buyer.getId(), product.getId()).orElse(null);

		if (oldCartItem != null) {
			return oldCartItem;
		}

		CartItem cartItem = CartItem
				.builder()
				.buyer(buyer)
				.product(product)
				.build();

		cartItemRepository.save(cartItem);

		return cartItem;
	}

	public List<CartItem> getItemsByBuyer(Member buyer) {
		return cartItemRepository.findAllByBuyerId(buyer.getId());
	}

	@Transactional
	public boolean removeItem(Member buyer, Product product) {
		CartItem oldCartItem = cartItemRepository.findByBuyerIdAndProductId(buyer.getId(), product.getId()).orElse(null);

		if (oldCartItem != null) {
			cartItemRepository.delete(oldCartItem);
			return true;
		}

		return false;
	}

	@Transactional
	public void removeItem(CartItem cartItem) {
		cartItemRepository.delete(cartItem);
	}

	public Optional<CartItem> findItemById(Long id) {
		return cartItemRepository.findById(id);
	}

	public boolean actorCanDelete(Member buyer, CartItem cartItem) {
		return buyer.getId().equals(cartItem.getBuyer().getId());
	}

	public List<CartItem> getCartItemsByBuyerIdProductIdIn(Long buyerId, long[] productIds) {
		return cartItemRepository.findAllByBuyerIdAndProductIdIn(buyerId, productIds);
	}
}
