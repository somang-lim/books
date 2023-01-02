package com.books.app.postKeyword.service;

import java.util.Optional;

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
	public PostKeyword save(String content) {
		Optional<PostKeyword> optKeyword = postKeywordRepository.findByContent(content);

		if (optKeyword.isPresent()) {
			return optKeyword.get();
		}

		PostKeyword postKeyword = PostKeyword
			.builder()
			.content(content)
			.build();

		postKeywordRepository.save(postKeyword);

		return postKeyword;
	}
}
