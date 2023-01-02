package com.books.app.postKeyword.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.postKeyword.entity.PostKeyword;

public interface PostKeywordRepository extends JpaRepository<PostKeyword, Long> {
	Optional<PostKeyword> findByContent(String keywordContent);
}
