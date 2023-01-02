package com.books.app.post.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.member.entity.Member;
import com.books.app.post.entity.Post;
import com.books.app.post.form.PostForm;
import com.books.app.post.repository.PostRepository;
import com.books.app.postTag.entity.PostTag;
import com.books.app.postTag.service.PostTagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PostService {
	private final PostRepository postRepository;
	private final PostTagService postTagService;

	// 글 작성 로직
	@Transactional
	public Post write(Member author, PostForm postForm) {
		Post post = Post
			.builder()
			.subject(postForm.getSubject())
			.content(postForm.getContent())
			.contentHtml(postForm.getContentHtml())
			.author(author)
			.build();

		postRepository.save(post);

		applyPostTags(post, postForm.getPostTagContents());

		return post;
	}

	// 해시태그 저장 로직
	public void applyPostTags(Post post, String postTagContents) {
		postTagService.applyPostTags(post, postTagContents);
	}

	// 글 조회 로직
	public Post detail(Long id) {
		Optional<Post> opPost = findById(id);

		if (opPost.isEmpty()) {
			return opPost.get();
		}

		Post post = opPost.get();

		List<PostTag> postTags = getPostTags(post);

		post.getExtra().put("postTags", postTags);

		return post;
	}

	// 글 해시태그 조회 로직
	public List<PostTag> getPostTags(Post post) {
		return postTagService.getPostTags(post);
	}

	public Optional<Post> findById(Long postId) {
		return postRepository.findById(postId);
	}

	public boolean actorCanModify(Member actor, Post post) {
		return actor.getId().equals(post.getAuthor().getId());
	}

	public boolean actorCanRemove(Member actor, Post post) {
		return actorCanModify(actor, post);
	}

	// 글 수정 로직
	@Transactional
	public void modify(Post post, PostForm postForm) {
		post.setSubject(postForm.getSubject());
		post.setContent(postForm.getContent());
		post.setContentHtml(postForm.getContentHtml());

		applyPostTags(post, postForm.getPostTagContents());
	}

	// 글 삭제 로직
	@Transactional
	public void remove(Post post) {
		if (post.getExtra().containsKey("hashTags")) {
			postTagService.remove(post);
		}

		postRepository.delete(post);
	}

	// 글 리스트 (해시태그 포함)
	public List<Post> list(Long authorId) {
		List<Post> posts = postRepository.findAllByAuthorIdOrderByIdDesc(authorId);

		loadForPrintData(posts);

		return posts;
	}

	public void loadForPrintData(List<Post> posts) {
		long[] ids = posts
			.stream()
			.mapToLong(Post::getId)
			.toArray();

		List<PostTag> postTagsByPostIds = postTagService.getPostTagsByPostIdIn(ids);

		Map<Long, List<PostTag>> postTagsByPostIdsMap = postTagsByPostIds
														.stream()
														.collect(groupingBy(
															postTag -> postTag.getPost().getId(), toList()
														));

		posts
			.stream()
			.forEach(post -> {
				List<PostTag> postTags = postTagsByPostIdsMap.get(post.getId());

				if (postTags == null || postTags.size() == 0) return;

				post.getExtra().put("postTags", postTags);
			});
	}
}
