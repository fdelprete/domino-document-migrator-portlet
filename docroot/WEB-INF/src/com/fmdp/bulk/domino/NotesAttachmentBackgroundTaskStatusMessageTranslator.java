package com.fmdp.bulk.domino;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageTranslator;
import com.liferay.portal.kernel.messaging.Message;

public class NotesAttachmentBackgroundTaskStatusMessageTranslator implements BackgroundTaskStatusMessageTranslator {

    @Override public void translate(
        BackgroundTaskStatus backgroundTaskStatus, Message message) {

    	backgroundTaskStatus.setAttribute(
    			"attachmentsImported",
    			message.getLong("attachmentsImported"));

    	backgroundTaskStatus.setAttribute(
    			"attachmentsWithProblem",
    			message.getLong("attachmentsWithProblem"));

     }
}
