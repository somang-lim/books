package com.books.app.emailSender.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

public interface EmailSenderService {
	void send(String to, String subject, String body);
}

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
class GmailEmailSenderService implements EmailSenderService {
	private final JavaMailSender mailSender;

	@Async
	@Override
	public void send(String to, String subject, String body) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, false, "UTF-8");
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			messageHelper.setText(body, true);
		} catch (MessagingException e) {
			log.info("message send fail");
		}

		mailSender.send(message);
	}
}
