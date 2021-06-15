package gaia3d.service.impl;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import gaia3d.config.PropertiesConfig;
import gaia3d.domain.notice.Mail;
import gaia3d.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 메일
 * @author hansang
 *
 */
@Service
public class MailServiceImpl implements NoticeService {

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	Configuration configuration;

	/**
	 * 전송
	 * @param mail
	 * @return
	 */
	public void send(Mail mail, PropertiesConfig propertiesConfig) throws Exception {

		MimeMessage mimeMessage;

		mimeMessage = new MimeMessage(javaMailSender.createMimeMessage().getSession());
		mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("gkstkd000@gmail.com"));
		mimeMessage.setFrom(new InternetAddress(mail.getAddress(),"관리자"));
		mimeMessage.setSubject("Welcome To SpringHow.com");

		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

		String emailContent = getEmailContent(mail);
		helper.setText(emailContent, true);


		javaMailSender.send(helper.getMimeMessage());
	}

	String getEmailContent(Mail mail) throws IOException, TemplateException {
		StringWriter stringWriter = new StringWriter();
		Map<String, Object> model = new HashMap<>();
		model.put("mail", mail);
		configuration.getTemplate("/notice/mail-template.ftlh").process(model, stringWriter);
		return stringWriter.getBuffer().toString();
	}

	@Bean
	public JavaMailSenderImpl mailSender() {

		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.host", "localhost");
		props.setProperty("mail.smtp.port", "125");
		props.setProperty("mail.debug", "true");
		props.setProperty("mail.smtp.starttls.enable", "false");
		props.setProperty("mail.smtp.auth", "true");
		//props.setProperty("mail.smtp.ssl.enable", "true");

		Session session = Session.getDefaultInstance(props, new Authenticator() {

			String un = "Postmaster";
			String pw = "admin";

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(un, pw);
			}
		});
		session.setDebug(true);
		javaMailSender.setSession(session);
		return javaMailSender;
	}

}
