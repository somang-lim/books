package com.books.app.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findAllByAuthorIdOrderByIdDesc(Long authorId);
}
