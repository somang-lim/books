package com.books.app.postTag.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.post.entity.Post;
import com.books.app.postKeyword.entity.PostKeyword;
import com.books.app.postKeyword.service.PostKeywordService;
import com.books.app.postTag.entity.PostTag;
import com.books.app.postTag.repository.PostTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostTagService {
	private final PostTagRepository postTagRepository;
	private final PostKeywordService postKeywordService;

	// 글 해시태그 반영 로직
	@Transactional
	public void applyPostTags(Post post, String postTagContents) {
		// 1. 기존 태그 가져오기
		List<PostTag> oldPostTags = getPostTags(post);

		// 2. 새로운 태그 키워드 리스트화
		List<String> postKeywordContents = Arrays.stream(postTagContents.split("#"))
			.map(String::trim)
			.filter(s -> s.length() > 0)
			.collect(Collectors.toList());

		// 3. 삭제할 태그 찾기 (기존 태그 리스트에서 새로운 태그 리스트에 없는 것)
		List<PostTag> needToDelete = new ArrayList<>();
		for (PostTag oldPostTag : oldPostTags) {
			// 기존에 등록된 태그가 새롭게 등록된 태그에 포함됐는지 여부 확인
			boolean contains = postKeywordContents.stream()
				.anyMatch(s -> s.equals(oldPostTag.getPostKeyword().getContent()));

			if (!contains) {
				needToDelete.add(oldPostTag);
			}
		}

		// 4. 삭제할 태그 리스트에서 하나씩 삭제
		needToDelete.forEach(postTag -> postTagRepository.delete(postTag));

		// 5. 새로운 태그 저장
		postKeywordContents.forEach(postKeywordContent -> {
			savePostTag(post, postKeywordContent);
		});
	}

	// 새로운 태그 저장
	private PostTag savePostTag(Post post, String postKeywordContent) {
		PostKeyword postKeyword = postKeywordService.save(postKeywordContent);

		Optional<PostTag> opPostTag = postTagRepository.findByPostIdAndPostKeywordId(post.getId(), postKeyword.getId());

		if (opPostTag.isPresent()) {
			return opPostTag.get();
		}

		PostTag postTag = PostTag
			.builder()
			.post(post)
			.member(post.getAuthor())
			.postKeyword(postKeyword)
			.build();

		postTagRepository.save(postTag);

		return postTag;
	}

	public List<PostTag> getPostTags(Post post) {
		return postTagRepository.findAllByPostId(post.getId());
	}

	public List<PostTag> getPostTagsByPostIdIn(long[] ids) {
		return postTagRepository.findAllByPostIdIn(ids);
	}
}
