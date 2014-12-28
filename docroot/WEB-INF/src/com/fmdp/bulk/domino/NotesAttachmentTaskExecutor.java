package com.fmdp.bulk.domino;

import lotus.domino.*;

import java.io.File;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import com.fmdp.domino_migrator.util.*;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.BackgroundTask;


public class NotesAttachmentTaskExecutor extends BaseBackgroundTaskExecutor {

	public NotesAttachmentTaskExecutor() {
		setBackgroundTaskStatusMessageTranslator(
				new NotesAttachmentBackgroundTaskStatusMessageTranslator ());
		setSerial(true);
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
			throws Exception {

		Map<String, Serializable> taskContextMap = backgroundTask.getTaskContextMap();


		String dominoHostName = MapUtil.getString(taskContextMap, "dominoHostName");
		String dominoUserName = MapUtil.getString(taskContextMap, "dominoUserName");
		String dominoUserPassword = MapUtil.getString(taskContextMap, "dominoUserPassword");
		String dominoDatabaseName = MapUtil.getString(taskContextMap, "dominoDatabaseName");
		String dominoViewName = MapUtil.getString(taskContextMap, "dominoViewName");
		String dominoFieldName = MapUtil.getString(taskContextMap, "dominoFieldName");
		String dominoFieldNameWithTags = MapUtil.getString(taskContextMap, "dominoFieldNameWithTags");
		boolean extractTags = MapUtil.getBoolean(taskContextMap, "extractTags");
		long newFolderId = MapUtil.getLong(taskContextMap, "newFolderId");
		long groupId = MapUtil.getLong(taskContextMap, "groupId");
		long userId = MapUtil.getLong(taskContextMap, "userId");
		
		if (Validator.isNull(dominoDatabaseName) ||
				Validator.isNull(dominoViewName) ||
				Validator.isNull(dominoFieldName)) {
			BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult(
					BackgroundTaskConstants.STATUS_FAILED);
			backgroundTaskResult.setStatusMessage("please-define-each-of-the-domino-properties-database-name,-view-name-and-form-name");

			//    	<liferay-ui:message key="please-define-each-of-the-domino-properties-database-name,-view-name-and-form-name" />
			//    	return;
		}
		DominoProxy dominoProxy = DominoProxy.getInstance();
		dominoProxy.openDominoSession(dominoHostName, dominoUserName, dominoUserPassword);


		if (!dominoProxy.isDominoSessionAvailable()) {
			BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult(
					BackgroundTaskConstants.STATUS_FAILED);
			backgroundTaskResult.setStatusMessage("error-connecting-to-the-domino-server");
		}


		String server = dominoProxy.dominoSession.getServerName();
		Database db = dominoProxy.dominoSession.getDatabase(server, dominoDatabaseName);
		if (!db.isOpen()) {
			BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult(
					BackgroundTaskConstants.STATUS_FAILED);
			backgroundTaskResult.setStatusMessage("the-database-does-not-exist-on-server");
		}
		View view = db.getView(dominoViewName);
		if (view == null ) {
			BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult(
					BackgroundTaskConstants.STATUS_FAILED);
			backgroundTaskResult.setStatusMessage("the-specified-view-does-not-exist-on-database");
		}
		
		int i = 0;
		Document doc = view.getFirstDocument();
		if (doc == null ) {
			BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult(
					BackgroundTaskConstants.STATUS_FAILED);
			backgroundTaskResult.setStatusMessage("no-doc-exists-on-specified-view");
		}
		String sourceFile = "";
		while (doc != null) {
			i++;
			BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult(
					BackgroundTaskConstants.STATUS_IN_PROGRESS);
			backgroundTaskResult.setStatusMessage("extracting files from " + doc.getUniversalID() + " - step " + i);
			
			String tagsFieldName = dominoFieldNameWithTags;
			if(doc.isValid() && doc.hasItem(dominoFieldName)) {
				RichTextItem  rtitem = (RichTextItem) doc.getFirstItem(dominoFieldName);
				Vector<?> v = rtitem.getEmbeddedObjects();
				//int size = v.size();
				Enumeration<?> e = v.elements();
				sourceFile = "";
				while (e.hasMoreElements()) {
					EmbeddedObject eo = (EmbeddedObject)e.nextElement();
					if (eo.getType() == EmbeddedObject.EMBED_ATTACHMENT) {
						sourceFile = eo.getSource();
						eo.extractFile(getTempDir() + sourceFile);
						File newFile = new File(getTempDir() + sourceFile);
						FileEntry fe = DocsAndMediaUtil.uploadFileToFolder(userId, groupId, newFolderId, newFile, sourceFile);
						if(extractTags && doc.hasItem(dominoFieldName)) {
							Item tagsNote = doc.getFirstItem(tagsFieldName);
							Vector<?> vi = tagsNote.getValues();
							String [] tags = vi.toArray(new String[vi.size()]);
							if (vi.size()>0) {
								DocsAndMediaUtil.addTags(userId, fe, tags);
							}
						}
						System.out.print(fe.getTitle() + StringPool.NEW_LINE);
					}
				}
				//    	<%="Document attached " + sourceFile %>
			}
			
			Document tmpdoc = doc;
			doc = view.getNextDocument(doc);
			tmpdoc.recycle();
		}
		view.recycle();
		db.recycle();
		dominoProxy.closeDominoSession();
		BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult(
				BackgroundTaskConstants.STATUS_SUCCESSFUL);
		backgroundTaskResult.setStatusMessage("tutto ok");
		return backgroundTaskResult;
	}
	@Override
	public String handleException(BackgroundTask backgroundTask, Exception e) {

		JSONObject exceptionMessagesJSONObject =
			JSONFactoryUtil.createJSONObject();
		
		String errorMessage = StringPool.BLANK;
		int errorType = BackgroundTaskConstants.STATUS_FAILED;
		
		if (e instanceof NotesException) {
			NotesException ne = (NotesException)e;
			errorMessage = ne.id + " - " + ne.getLocalizedMessage();
		} else {		
			errorMessage = e.getLocalizedMessage();
		}
		exceptionMessagesJSONObject.put("message", errorMessage);
		exceptionMessagesJSONObject.put("status", errorType);
		
		return exceptionMessagesJSONObject.toString();
	}
	private static String getTempDir() throws SystemException {
		File tempDir = null;	
			tempDir = new File(
					PrefsPropsUtil.getString(
							PropsKeys.UPLOAD_SERVLET_REQUEST_IMPL_TEMP_DIR, 
							SystemProperties.get(SystemProperties.TMP_DIR)));

		return tempDir.getAbsolutePath();
	}
}