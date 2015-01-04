package com.fmdp.bulk.domino;

import lotus.domino.*;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import com.fmdp.domino_migrator.portlet.model.NotesImportBean;
import com.fmdp.domino_migrator.util.DocsAndMediaUtil;
import com.fmdp.domino_migrator.util.DominoProxyUtil;
import com.fmdp.domino_migrator.util.NotesDocumentUtil;
import com.fmdp.domino_migrator.util.NotesImportDataHandlerStatusMessageSenderUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.BackgroundTask;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.asset.model.AssetVocabulary;


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
		String dominoFieldNameWithCategories = MapUtil.getString(taskContextMap, "dominoFieldNameWithCategories");
		String dominoFieldNameWithDescr = MapUtil.getString(taskContextMap, "dominoFieldNameWithDescr");
		String vocabularyName = MapUtil.getString(taskContextMap, "vocabularyName");
		boolean extractTags = MapUtil.getBoolean(taskContextMap, "extractTags");
		boolean extractCategories = MapUtil.getBoolean(taskContextMap, "extractCategories");
		boolean extractDescription = MapUtil.getBoolean(taskContextMap, "extractDescription");
		long newFolderId = MapUtil.getLong(taskContextMap, "newFolderId");
		long groupId = MapUtil.getLong(taskContextMap, "groupId");
		long userId = MapUtil.getLong(taskContextMap, "userId");
		String localeComp = MapUtil.getString(taskContextMap, "locale");;
		Locale locale = stringToLocale(localeComp);
		
		ServiceContext serviceContext = new ServiceContext();
		serviceContext.setScopeGroupId(groupId);

		
		if (Validator.isNull(dominoDatabaseName) ||
				Validator.isNull(dominoViewName) ||
				Validator.isNull(dominoFieldName)) {
			BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult(
					BackgroundTaskConstants.STATUS_FAILED);
			backgroundTaskResult.setStatusMessage("please-define-each-of-the-domino-properties-database-name,-view-name-and-form-name");
			return backgroundTaskResult;
		}

		DominoProxyUtil dominoProxy = DominoProxyUtil.getInstance();
		dominoProxy.openDominoSession(dominoHostName, dominoUserName, dominoUserPassword);

		if (!dominoProxy.isDominoSessionAvailable()) {
			BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult(
					BackgroundTaskConstants.STATUS_FAILED);
			backgroundTaskResult.setStatusMessage("error-connecting-to-the-domino-server");
			return backgroundTaskResult;
		}

		String server = dominoProxy.dominoSession.getServerName();
		Database db = dominoProxy.dominoSession.getDatabase(server, dominoDatabaseName);
		if (!db.isOpen()) {
			BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult(
					BackgroundTaskConstants.STATUS_FAILED);
			backgroundTaskResult.setStatusMessage("the-database-does-not-exist-on-server");
			return backgroundTaskResult;
		}
		View view = db.getView(dominoViewName);
		if (view == null ) {
			BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult(
					BackgroundTaskConstants.STATUS_FAILED);
			backgroundTaskResult.setStatusMessage("the-specified-view-does-not-exist-on-database");
			return backgroundTaskResult;
		}
		
		AssetVocabulary assetVocabulary = null;
		if (extractCategories) {
			assetVocabulary = DocsAndMediaUtil.createVocabulary(userId, groupId, locale, vocabularyName);
		}
		
		int notesDocProcessed = 0;
		Document doc = view.getFirstDocument();
		if (doc == null ) {
			BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult(
					BackgroundTaskConstants.STATUS_SUCCESSFUL);
			backgroundTaskResult.setStatusMessage("no-doc-exists-on-specified-view");
			return backgroundTaskResult;
		}
		
		NotesImportBean notesImportBean = new NotesImportBean();
		notesImportBean.setServerName(server);
		notesImportBean.setNotesDatabase(dominoDatabaseName);
		notesImportBean.setNotesView(dominoViewName);
		notesImportBean.setNotesFieldWithAttachments(dominoFieldName);
		notesImportBean.setNotesFieldWithTags(dominoFieldNameWithTags);
		notesImportBean.setNotesFieldWithCategories(dominoFieldNameWithCategories);
		notesImportBean.setNotesFieldWithDescr(dominoFieldNameWithDescr);
		notesImportBean.setVocabularyName(vocabularyName);
		notesImportBean.setDocumentsWithProblem(0);
		notesImportBean.setTotalDocuments(0);
		notesImportBean.setTotalAttachments(0);

		ViewEntryCollection vec = view.getAllEntries();
		
		int totDocs = vec.getCount();
		
//		System.out.print("totDocs " + totDocs + StringPool.NEW_LINE);
		notesImportBean.setTotalDocuments(totDocs);
		
		int numAttachments = 0;
		
		while (doc != null) {
			
			notesDocProcessed++;
			System.out.print("notesDocProcessed " + notesDocProcessed + StringPool.NEW_LINE);
			
			System.out.print("notesImportBean " + notesImportBean.toString() + StringPool.NEW_LINE);
			
			numAttachments += NotesDocumentUtil.ExtractAndSaveAttachment(userId, groupId, locale, doc, taskContextMap, assetVocabulary, serviceContext);
			
			notesImportBean.setDocumentsImported(notesDocProcessed);
			notesImportBean.setTotalAttachments(numAttachments);
			NotesImportDataHandlerStatusMessageSenderUtil.sendStatusMessage(notesImportBean);
			
			Document tmpdoc = doc;
			doc = view.getNextDocument(doc);
			tmpdoc.recycle();
		}
		notesImportBean.setTotalAttachments(numAttachments);
		NotesImportDataHandlerStatusMessageSenderUtil.sendStatusMessage(notesImportBean);
		
		view.recycle();
		db.recycle();
		if (dominoProxy.isDominoSessionAvailable())
			dominoProxy.closeDominoSession();
		BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult(
				BackgroundTaskConstants.STATUS_SUCCESSFUL);
		backgroundTaskResult.setStatusMessage(notesImportBean.getNotesImportBeanJSONArray().toString());
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
	
	private Locale stringToLocale(String s)
	{
		String l = "";
		String c = "";
	    StringTokenizer tempStringTokenizer = new StringTokenizer(s,",");
	    if(tempStringTokenizer.hasMoreTokens())
	    	l = (String)tempStringTokenizer.nextElement();
	    if(tempStringTokenizer.hasMoreTokens())
	    	c = (String) tempStringTokenizer.nextElement();
	    return new Locale(l,c);
	}
}