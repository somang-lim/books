package com.books.app.email.service;

import java.time.LocalDateTime;

import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.AppConfig;
import com.books.app.base.dto.RsData;
import com.books.app.email.entity.SendEmailLog;
import com.books.app.email.repository.SendEmailLogRepository;
import com.books.app.emailSender.service.EmailSenderService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailService {
	private final SendEmailLogRepository emailLogRepository;
	private final EmailSenderService emailSenderService;

	// 메일 발송 전체 로직
	@Transactional
	public RsData sendEmail(String email, String subject, String body) {
		SendEmailLog sendEmailLog = SendEmailLog
			.builder()
			.email(email)
			.subject(subject)
			.body(body)
			.build();

		emailLogRepository.save(sendEmailLog);

		RsData trySendRs = trySend(email, subject, body);

		setCompleted(sendEmailLog, trySendRs.getResultCode(), trySendRs.getMsg());

		return RsData.of("S-1", "메일이 발송되었습니다.", sendEmailLog.getId());
	}

	// 메일 발송하는 부분만 분리
	private RsData trySend(String email, String subject, String body) {
		// 운영상태가 아니라면, 메일 발송된 것으로 처리
		// if (AppConfig.isNotProd()) {
		// 	return RsData.of("S-0", "메일이 발송되었습니다.");
		// }

		try {
			emailSenderService.send(email, "no-reply@no-reply.com", subject, body);

			return RsData.of("S-1", "메일이 발송되었습니다.");
		} catch (MailException e) {
			return RsData.of("F-1", e.getMessage());
		}
	}

	// 메일 전송 상태에 따른 성공·실패 시간 로그에 입력
	@Transactional
	public void setCompleted(SendEmailLog sendEmailLog, String resultCode, String message) {
		if (resultCode.startsWith("S-")) {
			sendEmailLog.setSendEndDate(LocalDateTime.now());
		} else {
			sendEmailLog.setFailDate(LocalDateTime.now());
		}

		sendEmailLog.setResultCode(resultCode);
		sendEmailLog.setMessage(message);

		// emailLogRepository.save(sendEmailLog);
	}

}
