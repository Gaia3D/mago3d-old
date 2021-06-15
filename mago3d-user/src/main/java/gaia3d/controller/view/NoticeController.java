package gaia3d.controller.view;

import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.notice.Mail;
import gaia3d.domain.policy.Policy;
import gaia3d.service.impl.MailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 전송
 * 
 * @author hansang
 */
@Slf4j
@Controller
@RequestMapping("/notice")
public class NoticeController {

	@Autowired
	MailServiceImpl mailService;

	/**
	 * Sign in 페이지
	 * @return
	 */
	@GetMapping("/mail")
	public String dispMail() {
		return "/notice/mail";
	}

	/**
	 * Sign in 페이지
	 * @param mail
	 * @return
	 */
	@PostMapping("/mail")
	public String execMail(Mail mail) {

		Policy policy = CacheManager.getPolicy();
		try {
			mailService.send(mail, policy);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/notice/mail";
	}
}
