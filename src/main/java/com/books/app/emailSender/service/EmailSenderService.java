package com.books.app.emailSender.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

public interface EmailSenderService {
	void send(String to, String from, String subject, String body);
}

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class GmailEmailSenderService implements EmailSenderService {
	private final JavaMailSender mailSender;

	@Override
	public void send(String to, String from, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setFrom(from);
		message.setSubject(subject);
		message.setText(body);

		mailSender.send(message);
	}
}
