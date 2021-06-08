package gaia3d.service.impl;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.send.Mail;
import gaia3d.service.SendService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * 메일
 * @author hansang
 *
 */
@Service
public class MailServiceImpl implements SendService {

	/**
	 * 전송
	 * @param mail
	 * @return
	 */
	public void send(Mail mail, PropertiesConfig propertiesConfig) {

		JavaMailSenderImpl sender = new JavaMailSenderImpl();

		Properties prop = new Properties();
		prop.setProperty("mail.smtp.host", propertiesConfig.getMailHost());
		prop.setProperty("mail.smtp.port", propertiesConfig.getMailPort());
		sender.setJavaMailProperties(prop);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(mail.getAddress());
		message.setFrom(propertiesConfig.getMailAdminEmail());
		message.setSubject(mail.getTitle());
		message.setText(mail.getMessage());

		sender.send(message);
	}
}
