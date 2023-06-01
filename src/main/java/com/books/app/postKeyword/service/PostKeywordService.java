package com.books.app.postKeyword.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.postKeyword.entity.PostKeyword;
import com.books.app.postKeyword.repository.PostKeywordRepository;
import com.books.app.postTag.entity.PostTag;

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

	public PostKeyword getPostKeyword(PostTag postTag) {
		Optional<PostKeyword> optPostKeyword = postKeywordRepository.findById(postTag.getPostKeyword().getId());

		if (optPostKeyword.isPresent()) {
			return optPostKeyword.get();
		}

		return null;
	}

	@Transactional
	public void remove(List<PostKeyword> postKeywords) {
		for (PostKeyword postKeyword : postKeywords) {
			postKeywordRepository.delete(postKeyword);
		}
	}

	public Optional<PostKeyword> findById(Long postKeywordId) {
		return postKeywordRepository.findById(postKeywordId);
	}

	public List<PostKeyword> findByMemberId(Long authorId) {
		return postKeywordRepository.getQslAllByAuthorId(authorId);
	}
}
