package com.books.app.emailSender.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);

		mailSender.send(message);
	}
}
