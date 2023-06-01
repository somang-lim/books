package com.books.app.postTag.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.books.app.post.entity.Post;
import com.books.app.postKeyword.entity.PostKeyword;
import com.books.app.postTag.entity.PostTag;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
	List<PostTag> findAllByPostId(Long postId);

	Optional<PostTag> findByPostIdAndPostKeywordId(Long postId, Long postKeywordId);

	List<PostTag> findAllByPostIdIn(long[] ids);

	List<PostTag> findByPostKeywordId(Long postKeywordId);

	void deleteByPostId(Long id);

	List<PostTag> findAllByMemberIdAndPostKeywordIdOrderByPost_idDesc(Long authorId, Long postKeywordId);
}
