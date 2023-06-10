package com.books.app.myBook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.myBook.entity.MyBook;

public interface MyBookRepository extends JpaRepository<MyBook, Long> {

	void deleteByProductIdAndOwnerId(Long productId, Long ownerId);

}
