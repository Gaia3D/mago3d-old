package gaia3d.service;

import gaia3d.domain.send.Mail;

/**
 * 사용자
 * @author jeongdae
 *
 */
public interface SendService {

	/**
	 * 전송
	 * @param mail
	 * @return
	 */

	void mailSend(Mail mail);

}
