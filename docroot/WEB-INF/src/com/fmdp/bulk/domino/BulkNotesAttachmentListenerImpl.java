package com.fmdp.bulk.domino;

import java.util.List;
import javax.mail.internet.InternetAddress;
import com.liferay.mail.service.MailServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
public class BulkNotesAttachmentListenerImpl implements MessageListener {
	public void receive(Message message) {
		try {
			doReceive(message);
		}
		catch (Exception e) {
			_log.error("Unable to process message " + message, e);
		}
	}
	protected void doReceive(Message message)
			throws Exception {
		String mailSubject = (String)message.get("mailSubject");
		String mailBody = (String)message.get("mailBody");
		String senderEmailAddess =(String)message.get("senderEmailAddess");
		List<User> usersList=UserLocalServiceUtil.getUsers(1,UserLocalServiceUtil.getUsersCount());
		MailMessage mailMessage=new MailMessage();
		mailMessage.setBody(mailBody);
		mailMessage.setSubject(mailSubject);
		mailMessage.setFrom(new InternetAddress(senderEmailAddess));
		for(User user:usersList){
			mailMessage.setTo(new InternetAddress(user.getEmailAddress()));
			MailServiceUtil.sendEmail(mailMessage);
			System.out.println("mail sent to..::============:"+user.getEmailAddress());
		}

	}
	private static Log _log =
			LogFactoryUtil.getLog(BulkNotesAttachmentListenerImpl.class);
}