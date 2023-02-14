package com.books.app.post.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.member.entity.Member;
import com.books.app.member.repository.MemberRepository;
import com.books.app.post.entity.Post;
import com.books.app.post.form.PostForm;
import com.books.app.postTag.entity.PostTag;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PostServiceTests {

	@Autowired
	private PostService postService;

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("글 작성")
	void write() {
		Member author = memberRepository.findByUsername("user1").get();

		PostForm postForm = new PostForm("제목", "내용", "내용", "#IT #IT_특집기사 #백엔드");
		Post post = postService.write(author, postForm);

		assertThat(post).isNotNull();
		assertThat(post.getSubject()).isEqualTo("제목");
		assertThat(post.getContent()).isEqualTo("내용");
		assertThat(post.getContentHtml()).isEqualTo("내용");

		List<PostTag> postTags = postService.getPostTags(post);

		postTags.forEach(postTag ->
			assertThat(postTag.getPostKeyword().getContent()).containsAnyOf("IT", "IT_특집기사", "백엔드"));
	}

	@Test
	@DisplayName("글 수정")
	void modify() {
		Post post = postService.findById(1L).get();

		PostForm postForm = new PostForm("제목 NEW", "내용 NEW", "내용 NEW", "#IT #일반기사 프론트엔드");
		postService.modify(post, postForm);

		assertThat(post).isNotNull();
		assertThat(post.getSubject()).isEqualTo("제목 NEW");
		assertThat(post.getContent()).isEqualTo("내용 NEW");
		assertThat(post.getContentHtml()).isEqualTo("내용 NEW");

		List<PostTag> postTags = postService.getPostTags(post);

		postTags.forEach(postTag ->
			assertThat(postTag.getPostKeyword().getContent()).containsAnyOf("IT", "일반기사", "프론트엔드"));
	}

}
