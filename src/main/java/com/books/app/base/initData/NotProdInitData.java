package com.books.app.base.initData;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.books.app.member.entity.Member;
import com.books.app.member.form.JoinForm;
import com.books.app.member.service.MemberService;
import com.books.app.post.form.PostForm;
import com.books.app.post.service.PostService;

@Configuration
@Profile({"dev", "test"})
public class NotProdInitData {
	// initData가 2번 이상 실행되지 않도록 설정
	private boolean initDataDone = false;

	@Bean
	CommandLineRunner initData(
		MemberService memberService,
		PostService postService
	) {
		return args -> {
			if (initDataDone) {
				return;
			}

			initDataDone = true;

			JoinForm joinForm = new JoinForm("user1", "1234", "user1@test.com");
			Member member1 = memberService.join(joinForm);
			memberService.forceEmailVerify(member1);
			memberService.beAuthor(member1, "망소");
			joinForm = new JoinForm("user2", "1234", "user2@test.com");
			Member member2 = memberService.join(joinForm);
			memberService.forceEmailVerify(member2);

			PostForm postForm1 = new PostForm("자바를 우아하게 사용하는 방법", "# 내용 1", "<h1>내용 1</h1>", "#IT #자바 #카프카");
			postService.write(member1, postForm1);

			PostForm postForm2 = new PostForm(
					"자바스크립트를 우아하게 사용하는 방법",
                            """
                            # 자바스크립트는 이렇게 쓰세요.

                            ```js
                            const a = 10;
                            console.log(a);
                            ```
                            """.stripIndent(),
							"""
								<h1>자바스크립트는 이렇게 쓰세요.</h1><div data-language="js" class="toastui-editor-ww-code-block-highlighting"><pre class="language-js"><code data-language="js" class="language-js"><span class="token keyword">const</span> a <span class="token operator">=</span> <span class="token number">10</span><span class="token punctuation">;</span>
								<span class="token console class-name">console</span><span class="token punctuation">.</span><span class="token method function property-access">log</span><span class="token punctuation">(</span>a<span class="token punctuation">)</span><span class="token punctuation">;</span></code></pre></div>
							""".stripIndent(),
			 "#IT #프론트엔드 #리액트");
			postService.write(member1, postForm2);
		};
	}
}
