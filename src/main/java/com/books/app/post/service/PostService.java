package com.books.app.post.service;

import java.util.List;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
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

	public Optional<Post> findById(Long id) {
		return postRepository.findById(id);
	}

	public boolean actorCanModify(Member actor, Post post) {
		return actor.getId().equals(post.getAuthor().getId());
	}

	public boolean actorCanRemove(Member actor, Post post) {
		return actorCanModify(actor, post);
	}
}
