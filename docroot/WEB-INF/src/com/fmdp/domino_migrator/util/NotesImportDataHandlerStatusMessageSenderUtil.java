package com.fmdp.domino_migrator.util;

import com.fmdp.domino_migrator.portlet.model.NotesImportBean;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;

public class NotesImportDataHandlerStatusMessageSenderUtil {

	public static void sendStatusMessage(
			NotesImportBean messageContent) {

			if (!BackgroundTaskThreadLocal.hasBackgroundTask()) {
				return;
			}

			Message message = createMessage(messageContent);

			MessageBusUtil.sendMessage(DestinationNames.BACKGROUND_TASK_STATUS, message);
		}
	
	protected static Message createMessage(NotesImportBean messageContent) {

			Message message = new Message();

			message.put(
				"backgroundTaskId",
				BackgroundTaskThreadLocal.getBackgroundTaskId());
			
			message.put("totalDocuments", messageContent.getTotalDocuments());

			message.put("totalAttachments", messageContent.getTotalAttachments());

			message.put("documentsImported", messageContent.getDocumentsImported());

			message.put("documentsWithProblem", messageContent.getDocumentsWithProblem());

			return message;
		}

}
