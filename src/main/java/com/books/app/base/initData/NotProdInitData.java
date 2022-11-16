package com.books.app.base.initData;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.books.app.member.entity.Member;
import com.books.app.member.form.JoinForm;
import com.books.app.member.service.MemberService;

@Configuration
@Profile({"dev", "test"})
public class NotProdInitData {
	private boolean initDataDone = false;

	@Bean
	CommandLineRunner initData(
		MemberService memberService
	) {
		return args -> {
			if (initDataDone) {
				return;
			}

			initDataDone = true;

			JoinForm joinForm = new JoinForm("user1", "1234", "user1@test.com");
			Member member1 = memberService.join(joinForm);
			memberService.forceEmailVerify(member1);
			joinForm = new JoinForm("user2", "1234", "user2@test.com");
			Member member2 = memberService.join(joinForm);
			memberService.forceEmailVerify(member2);

		};
	}
}
