package com.books.app.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.member.entity.Member;
import com.books.app.post.entity.Post;
import com.books.app.post.form.PostForm;
import com.books.app.post.repository.PostRepository;
import com.books.app.postTag.service.PostTagService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final PostTagService postTagService;

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

	public void applyPostTags(Post post, String postTagContents) {
		postTagService.applyPostTags(post, postTagContents);
	}
}
