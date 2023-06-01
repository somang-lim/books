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
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
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
		List<PostTag> needToDeleteTags = new ArrayList<>();
		for (PostTag oldPostTag : oldPostTags) {
			// 3-1. 기존에 등록된 태그가 새롭게 등록된 태그에 포함됐는지 여부 확인
			boolean contains = postKeywordContents.stream()
				.anyMatch(s -> s.equals(oldPostTag.getPostKeyword().getContent()));

			// 3-2. 없다면, 삭제할 태그 리스트에 추가하기
			if (!contains) {
				needToDeleteTags.add(oldPostTag);
			}
		}

		// 4. 삭제할 키워드 찾기
		List<PostKeyword> needToDeleteKeywords = new ArrayList<>();
		for (PostTag postTag : needToDeleteTags) {
			// 4-1. 글의 키워드 하나 가져오기
			PostKeyword postKeyword = postKeywordService.getPostKeyword(postTag);

			// 4-2. 키워드가 없으면 멈추기
			if (postKeyword == null) {
				break;
			}

			// 4-3. 키워드가 몇 개 있는지 확인하기
			List<PostTag> postTags = postTagRepository.findByPostKeywordId(postKeyword.getId());

			// 4-4. 키워드가 1개만 있으면, 삭제할 키워드 리스트에 추가하기
			if (postTags.size() == 1) {
				needToDeleteKeywords.add(postTag.getPostKeyword());
			}
		}

		// 5. 사용하지 않는 태그가 있다면 키워드 먼저 삭제하기
		if (needToDeleteKeywords.size() > 0) {
			deletePostKeyword(needToDeleteKeywords);
		}

		// 6. 삭제할 태그 리스트에서 하나씩 삭제
		needToDeleteTags.forEach(postTag -> postTagRepository.delete(postTag));

		// 7. 새로운 태그 저장
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

	public List<PostTag> getPostTags(Long authorId, Long postKeywordId) {
		return postTagRepository.findAllByMemberIdAndPostKeywordIdOrderByPost_idDesc(authorId, postKeywordId);
	}

	public List<PostTag> getPostTagsByPostIdIn(long[] ids) {
		return postTagRepository.findAllByPostIdIn(ids);
	}

	// 글 삭제 시 사용하지 않는 태그 삭제
	@Transactional
	public void remove(Post post) {
		// 1. 기존 태그 가져오기
		List<PostTag> oldPostTags = getPostTags(post);

		// 2. 글의 해시태그가 쓰인 곳이 있는지 확인하기
		List<PostKeyword> needToDelete = new ArrayList<>();

		for (PostTag postTag : oldPostTags) {
			// 2-1. 글의 키워드 하나를 가져오기
			PostKeyword postKeyword = postKeywordService.getPostKeyword(postTag);

			// 2-2. 키워드가 없으면 멈추기
			if (postKeyword == null) {
				break;
			}

			// 2-3. 키워드가 몇 개 있는지 확인하기
			List<PostTag> postTags = postTagRepository.findByPostKeywordId(postKeyword.getId());

			// 2-4. 키워드가 1개만 있으면, 삭제할 키워드 리스트에 추가하기
			if (postTags.size() == 1) {
				needToDelete.add(postTag.getPostKeyword());
			}
		}

		// 3. 사용하지 않는 태그가 있다면 키워드 먼저 삭제하기
		if (needToDelete.size() > 0) {
			deletePostKeyword(needToDelete);
		}

		// 글의 태그 삭제
		postTagRepository.deleteByPostId(post.getId());
	}

	// 사용하지 않는 태그의 키워드도 사용하지 않을 경우 삭제
	private void deletePostKeyword(List<PostKeyword> postKeywords) {
		postKeywordService.remove(postKeywords);
	}
}
