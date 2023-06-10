package com.books.app.base.initData;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.books.app.cart.service.CartService;
import com.books.app.member.entity.Member;
import com.books.app.member.form.JoinForm;
import com.books.app.member.service.MemberService;
import com.books.app.order.entity.Order;
import com.books.app.order.repository.OrderRepository;
import com.books.app.order.service.OrderService;
import com.books.app.post.form.PostForm;
import com.books.app.post.service.PostService;
import com.books.app.product.entity.Product;
import com.books.app.product.form.ProductForm;
import com.books.app.product.service.ProductService;

@Configuration
@Profile({"dev", "test"})
public class NotProdInitData {
	// initData가 2번 이상 실행되지 않도록 설정
	private boolean initDataDone = false;

	@Bean
	CommandLineRunner initData(
		MemberService memberService,
		PostService postService,
		ProductService productService,
		CartService cartService,
		OrderService orderService,
		OrderRepository orderRepository
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
			memberService.beAuthor(member2, "mark");

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

			PostForm postForm3 = new PostForm("제목 3", "내용 3", "내용 3", "#IT# 프론트엔드 #HTML #CSS");
			postService.write(member2, postForm3);

			PostForm postForm4 = new PostForm("제목 4", "내용 4", "내용 4", "#IT #스프링부트 #자바");
			postService.write(member2, postForm4);

			PostForm postForm5 = new PostForm("제목 5", "내용 5", "내용 5", "#IT #자바 #카프카");
			postService.write(member1, postForm5);

			PostForm postForm6 = new PostForm("제목 6", "내용 6", "내용 6", "#IT #프론트엔드 #REACT");
			postService.write(member1, postForm6);

			PostForm postForm7 = new PostForm("제목 7", "내용 7", "내용 7", "#IT# 프론트엔드 #HTML #CSS");
			postService.write(member2, postForm7);

			PostForm postForm8 = new PostForm("제목 8", "내용 8", "내용 8", "#IT #스프링부트 #자바");
			postService.write(member2, postForm8);


			Product product1 = productService.create(member1, "상품명1 상품명1 상품명1 상품명1 상품명1 상품명1", 30_000, "카프카", "#IT #카프카");
			Product product2 = productService.create(member2, "상품명2", 40_000, "스프링부트", "#IT #REACT");
			Product product3 = productService.create(member1, "상품명3", 50_000, "REACT", "#IT #REACT");
			Product product4 = productService.create(member2, "상품명4", 60_000, "HTML", "#IT #HTML");


			memberService.addCash(member1, 10_000, "충전__무통장입금");
			memberService.addCash(member1, 20_000, "충전__무통장입금");
			memberService.addCash(member1, -5_000, "출금__일반");
			memberService.addCash(member1, 1_000_000, "충전__무통장입금");
			memberService.addCash(member2, 2_000_000, "충전__무통장입금");


			class Helper {
				public Order order(Member member, List<Product> products) {
					for (int i = 0; i < products.size(); i++) {
						Product product = products.get(i);

						cartService.addItem(member, product);
					}

					return orderService.createFromCart(member);
				}
			}

			Helper helper = new Helper();

			Order order1 = helper.order(member2, Arrays.asList(
							product1,
							product3
				)
			);
		};
	}
}
