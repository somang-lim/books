package com.books.app.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
