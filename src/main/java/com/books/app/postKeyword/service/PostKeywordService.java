package com.books.app.postKeyword.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.postKeyword.entity.PostKeyword;
import com.books.app.postKeyword.repository.PostKeywordRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostKeywordService {
	private final PostKeywordRepository postKeywordRepository;

	@Transactional
	public PostKeyword save(String postKeywordContent) {
		PostKeyword postKeyword = PostKeyword
			.builder()
			.content(postKeywordContent)
			.build();

		postKeywordRepository.save(postKeyword);

		return postKeyword;
	}
}
