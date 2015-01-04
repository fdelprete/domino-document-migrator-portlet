package com.fmdp.domino_migrator.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.asset.model.AssetVocabulary;

import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;

public class NotesDocumentUtil {

	private static final char[] INVALID_CHARACTERS = new char[] {
		CharPool.AMPERSAND, CharPool.APOSTROPHE, CharPool.AT,
		CharPool.BACK_SLASH, CharPool.CLOSE_BRACKET, CharPool.CLOSE_CURLY_BRACE,
		CharPool.COLON, CharPool.COMMA, CharPool.EQUAL, CharPool.GREATER_THAN,
		CharPool.FORWARD_SLASH, CharPool.LESS_THAN, CharPool.NEW_LINE,
		CharPool.OPEN_BRACKET, CharPool.OPEN_CURLY_BRACE, CharPool.PERCENT,
		CharPool.PIPE, CharPool.PLUS, CharPool.POUND, CharPool.PRIME,
		CharPool.QUESTION, CharPool.QUOTE, CharPool.RETURN, CharPool.SEMICOLON,
		CharPool.SLASH, CharPool.STAR, CharPool.TILDE
	};
	
	public static int ExtractAndSaveAttachment(long userId, long groupId, long newFolderId, Document doc, String fieldNameWithAttachment, 
			boolean extractTags, String fieldNameWithTags,
			boolean extractCategories, String fieldNameWithCategories, String vocabularyName,
			boolean extractDescription, String dominoFieldNameWithDescr) 
					throws NotesException, PortalException, SystemException, FileNotFoundException {
		int numAttachments = 0;
		String documentDescription = "";
		if(doc.isValid() && doc.hasItem(fieldNameWithAttachment)) {
			
			if (extractDescription && doc.hasItem(dominoFieldNameWithDescr))
				documentDescription = doc.getItemValueString(dominoFieldNameWithDescr);
			
			RichTextItem  rtitem = (RichTextItem) doc.getFirstItem(fieldNameWithAttachment);
			Vector<?> v = rtitem.getEmbeddedObjects();

			Enumeration<?> e = v.elements();
			String sourceFile = "";
			while (e.hasMoreElements()) {
				EmbeddedObject eo = (EmbeddedObject)e.nextElement();
				if (eo.getType() == EmbeddedObject.EMBED_ATTACHMENT) {
					sourceFile = eo.getSource();
					eo.extractFile(getTempDir() + sourceFile);
					
					numAttachments++;
					
					File newFile = new File(getTempDir() + sourceFile);
					FileEntry fileEntry = DocsAndMediaUtil.uploadFileToFolder(userId, groupId, 
							newFolderId, newFile, sourceFile,
							documentDescription);

					try {
						//ExtractAndSaveTags(userId, doc, extractTags, fieldNameWithTags, fileEntry);
					} catch (Exception e1) {
						_log.info(
								"Error extracting tags "
								+ "for doc with universalId = " + doc.getUniversalID());
						e1.printStackTrace();
					}
					
					System.out.print(fileEntry.getTitle() + StringPool.NEW_LINE);
				}
			}
		}
		return numAttachments;
	}

	public static int ExtractAndSaveAttachment(long userId, 
			long groupId, 
			Locale locale,
			Document doc,
			java.util.Map<java.lang.String, java.io.Serializable> taskContextMap,
			AssetVocabulary assetVocabulary,
			ServiceContext serviceContext) 
					throws NotesException, PortalException, SystemException, FileNotFoundException {
		int numAttachments = 0;
		String documentDescription = "";
		
		String fieldNameWithAttachment = MapUtil.getString(taskContextMap, "dominoFieldName");
		String dominoFieldNameWithTags = MapUtil.getString(taskContextMap, "dominoFieldNameWithTags");
		String dominoFieldNameWithCategories = MapUtil.getString(taskContextMap, "dominoFieldNameWithCategories");
		String dominoFieldNameWithDescr = MapUtil.getString(taskContextMap, "dominoFieldNameWithDescr");
		boolean extractTags = MapUtil.getBoolean(taskContextMap, "extractTags");
		boolean extractCategories = MapUtil.getBoolean(taskContextMap, "extractCategories");
		boolean extractDescription = MapUtil.getBoolean(taskContextMap, "extractDescription");
		long newFolderId = MapUtil.getLong(taskContextMap, "newFolderId");
		
		if(doc.isValid() && doc.hasItem(fieldNameWithAttachment)) {
			
			if (extractDescription && doc.hasItem(dominoFieldNameWithDescr))
				documentDescription = doc.getItemValueString(dominoFieldNameWithDescr);
			
			RichTextItem  rtitem = (RichTextItem) doc.getFirstItem(fieldNameWithAttachment);
			Vector<?> v = rtitem.getEmbeddedObjects();

			Enumeration<?> e = v.elements();
			String sourceFile = "";
			while (e.hasMoreElements()) {
				EmbeddedObject eo = (EmbeddedObject)e.nextElement();
				if (eo.getType() == EmbeddedObject.EMBED_ATTACHMENT) {
					sourceFile = eo.getSource();
					eo.extractFile(getTempDir() + sourceFile);
					
					numAttachments++;
					
					File newFile = new File(getTempDir() + sourceFile);
					FileEntry fileEntry = DocsAndMediaUtil.uploadFileToFolder(userId, groupId, 
							newFolderId, newFile, sourceFile,
							documentDescription);
					List<String> tagList = null;
					if(extractTags) {
						try {
							tagList = ExtractTags(userId, doc, dominoFieldNameWithTags);
						} catch (Exception e1) {
							_log.info(
									"Error extracting tags "
									+ "for doc with universalId = " + doc.getUniversalID());
							e1.printStackTrace();
						}					
					}

					List<String> catList = null;
					if(extractCategories) {
						try {
							catList = ExtractCategories(userId, groupId, locale, doc, 
									dominoFieldNameWithCategories, assetVocabulary, serviceContext);
						} catch (Exception e1) {
							_log.info(
									"Error extracting categories "
									+ "for doc with universalId = " + doc.getUniversalID());
							e1.printStackTrace();
						}					
					}
					
					if (tagList.size() > 0 || catList.size() > 0) {
						String [] tags = tagList.toArray(new String[tagList.size()]);
						long[] catsLong = new long[catList.size()];     
						for (int i = 0; i < catList.size(); i++) {     
							catsLong[i] = Long.parseLong(catList.get(i + 1));     
						}  
						try {
							DocsAndMediaUtil.addTags(userId, fileEntry, catsLong, tags);
						} catch (Exception e1) {
							_log.info(
									"Error savings tags and categories in fileEntry "
									+ "for doc with universalId = " + doc.getUniversalID());
							e1.printStackTrace();
						}
					}
					System.out.print(fileEntry.getTitle() + StringPool.NEW_LINE);
				}
			}
		}
		return numAttachments;
	}


	private static List<String> ExtractTags(long userId, Document doc, String fieldNameWithTags) 
			throws Exception {

		if(doc.hasItem(fieldNameWithTags)) {
			Item tagsNote = doc.getFirstItem(fieldNameWithTags);
			Vector<?> vi = tagsNote.getValues();
			if (vi.size() > 0 ) {
				String [] tags = vi.toArray(new String[vi.size()]);
				List<String> tagList = new ArrayList<String>(Arrays.asList(tags));
				
				for (String tag : tagList) {
					if (!isValidWord(tag)) {
						tagList.removeAll(Arrays.asList(tag));
					}
				}
				return tagList;
			}
		}
		return null;
	}

	private static List<String> ExtractCategories(long userId, long groupId, Locale locale, Document doc, 
			String dominoFieldNameWithCategories, AssetVocabulary assetVocabulary, ServiceContext serviceContext) 
			throws Exception {
		
		String catSeparator = "" + CharPool.BACK_SLASH;
		List<String> returnedIds = new ArrayList<String>();
		if(doc.hasItem(dominoFieldNameWithCategories)) {
			Item tagsNote = doc.getFirstItem(dominoFieldNameWithCategories);
			List<String> catsList = tagsNote.getValues();
				for (String cat : catsList) {
					String []  separatedCats = cat.split(catSeparator);
					List<String> separatedCatsList = new ArrayList<String>(Arrays.asList(separatedCats));
					List<String> catIds = DocsAndMediaUtil.getChildCategory(userId, groupId, locale, 
																				separatedCatsList, 1, 0, 
																				assetVocabulary.getVocabularyId(), serviceContext);
					returnedIds.addAll(catIds);
				}		
		}
		return returnedIds;
	
	}

	private static String getTempDir() throws SystemException {
		File tempDir = null;	
			tempDir = new File(
					PrefsPropsUtil.getString(
							PropsKeys.UPLOAD_SERVLET_REQUEST_IMPL_TEMP_DIR, 
							SystemProperties.get(SystemProperties.TMP_DIR)));

		return tempDir.getAbsolutePath();
	}

	private static boolean isValidWord(String word) {
		if (Validator.isNull(word)) {
			return false;
		}

		char[] wordCharArray = word.toCharArray();

		for (char c : wordCharArray) {
			for (char invalidChar : INVALID_CHARACTERS) {
				if (c == invalidChar) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Word " + word + " is not valid because " + c +
								" is not allowed");
					}

					return false;
				}
			}
		}

		return true;
	}
	private static Log _log = LogFactoryUtil.getLog(DocsAndMediaUtil.class);

}
