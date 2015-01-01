package com.fmdp.bulk.domino;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageTranslator;
import com.liferay.portal.kernel.messaging.Message;

public class NotesAttachmentBackgroundTaskStatusMessageTranslator implements BackgroundTaskStatusMessageTranslator {

    /* (non-Javadoc)
     * @see com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageTranslator#translate(com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus, com.liferay.portal.kernel.messaging.Message)
     */
    @Override public void translate(
        BackgroundTaskStatus backgroundTaskStatus, Message message) {

    	backgroundTaskStatus.setAttribute(
    			"documentsImported",
    			message.getLong("documentsImported"));

    	backgroundTaskStatus.setAttribute(
    			"documentsWithProblem",
    			message.getLong("documentsWithProblem"));
    	
    	backgroundTaskStatus.setAttribute(
    			"totalDocuments",
    			message.getLong("totalDocuments"));
    	
    	backgroundTaskStatus.setAttribute(
    			"totalAttachments",
    			message.getLong("totalAttachments"));
    	
     }
}
