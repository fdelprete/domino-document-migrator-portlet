package com.fmdp.bulk.domino;

import java.io.IOException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
public class BulkNotesAttachmentAction extends MVCPortlet {
	public void sendMessage(
			ActionRequest actionRequest, ActionResponse actionResponse)
					throws IOException, PortletException {
		System.out.println("====sendMessage===");
		String mailSubject=ParamUtil.getString(actionRequest,"mailSubject");
		String mailBody=ParamUtil.getString(actionRequest,"mailBody");
		String senderMailAddress=mailBody=ParamUtil.getString(actionRequest,"senderEmailAddess");
		Message message = new Message();
		message.put("mailSubject",mailSubject);
		message.put("mailBody",mailBody);
		message.put("senderMailAddress",senderMailAddress);
		try {
			MessageBusUtil.sendMessage("fmdp/bulk/notesattachment/destination", message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}