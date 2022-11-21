package com.books.app.postTag.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.books.app.postTag.entity.PostTag;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
	List<PostTag> findAllByPostId(Long postId);

	Optional<PostTag> findByPostIdAndPostKeywordId(Long postId, Long postKeywordId);
}
