package gaia3d.service;

import gaia3d.domain.notice.Mail;
import gaia3d.domain.policy.Policy;

/**
 * 사용자
 * @author jeongdae
 *
 */
public interface NoticeService {

	/**
	 * 전송
	 * @param mail
	 * @return
	 */

	void send(Mail mail, Policy policy) throws Exception;

}
