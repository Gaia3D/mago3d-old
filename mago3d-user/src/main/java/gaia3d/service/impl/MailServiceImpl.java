package gaia3d.service.impl;

import gaia3d.domain.send.Mail;
import gaia3d.service.SendService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

/**
 * 사용자
 * @author jeongdae
 *
 */
@Service
public class MailServiceImpl implements SendService {

	//private JavaMailSender mailSender;


	private static final String FROM_ADDRESS = "gkstkd000@gmail.com";

	/**
	 * 전송
	 * @param mail
	 * @return
	 */
	public void mailSend(Mail mail) {
		JavaMailSender mailSender = new JavaMailSenderImpl();
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(mail.getAddress());
		message.setFrom(MailServiceImpl.FROM_ADDRESS);
		message.setSubject(mail.getTitle());
		message.setText(mail.getMessage());

		mailSender.send(message);
	}
}
