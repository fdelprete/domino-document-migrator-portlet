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
	
	/**
	 * @param userId
	 * @param groupId
	 * @param newFolderId
	 * @param doc
	 * @param fieldNameWithAttachment
	 * @param extractTags
	 * @param fieldNameWithTags
	 * @param extractCategories
	 * @param fieldNameWithCategories
	 * @param vocabularyName
	 * @param extractDescription
	 * @param dominoFieldNameWithDescr
	 * @return
	 * @throws NotesException
	 * @throws PortalException
	 * @throws SystemException
	 * @throws FileNotFoundException
	 */
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

	/**
	 * @param userId
	 * @param groupId
	 * @param locale
	 * @param doc = The Notes document 
	 * @param taskContextMap
	 * @param assetVocabulary = The Liferay vocabulary name to create or to use (if exists)
	 * @param serviceContext
	 * @return = the number of attachments  extracted
	 * @throws NotesException
	 * @throws PortalException
	 * @throws SystemException
	 * @throws FileNotFoundException
	 */
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
		
		/*
		 * Initializing some parameters from the contextMap
		 */
		String fieldNameWithAttachment = MapUtil.getString(taskContextMap, "dominoFieldName");
		String dominoFieldNameWithTags = MapUtil.getString(taskContextMap, "dominoFieldNameWithTags");
		String dominoFieldNameWithCategories = MapUtil.getString(taskContextMap, "dominoFieldNameWithCategories");
		String dominoFieldNameWithDescr = MapUtil.getString(taskContextMap, "dominoFieldNameWithDescr");
		String dominoFieldNameWithTitle = MapUtil.getString(taskContextMap, "dominoFieldNameWithTitle");
		boolean extractTags = MapUtil.getBoolean(taskContextMap, "extractTags");
		boolean extractCategories = MapUtil.getBoolean(taskContextMap, "extractCategories");
		boolean extractDescription = MapUtil.getBoolean(taskContextMap, "extractDescription");
		long newFolderId = MapUtil.getLong(taskContextMap, "newFolderId");
		
		/*
		 * If the document exists but has been deleted then doc.isValid() return false
		 */
		
		if(doc.isValid() && doc.hasItem(fieldNameWithAttachment)) {
			
			/*
			 * Should we extract description?
			 */
			if (extractDescription && doc.hasItem(dominoFieldNameWithDescr))
				documentDescription = doc.getItemValueString(dominoFieldNameWithDescr);
			
			/*
			 * START attachment(s) extraction
			 */
			RichTextItem  rtitem = (RichTextItem) doc.getFirstItem(fieldNameWithAttachment);
			Vector<?> v = rtitem.getEmbeddedObjects();

			/*
			 * DONE: if there are more than one attachment, then I create a folder in 
			 * Documents and Media with the same name of the Notes document Title and the I'll put here
			 * the extracted attachments 
			 */
			Enumeration<?> e = v.elements();
			int howManyEmbedAttachment = 0;
			while (e.hasMoreElements()) {
				EmbeddedObject eo = (EmbeddedObject)e.nextElement();
				if (eo.getType() == EmbeddedObject.EMBED_ATTACHMENT) {
					howManyEmbedAttachment++;
				}
			}
			long folderIdToUse;
			if (howManyEmbedAttachment > 1) {
				/*
				 * OK I found two or more attachments: so we create a subfolder in the main folder
				 * the new folder name will be the value of the dominoFieldNameWithTitle (only first 100 chars)
				 */
				long repositoryId = DocsAndMediaUtil.getRepositoryIdFromFloderId(newFolderId);
				String folderTitle = toWord(dominoFieldNameWithTitle); // not allowed chars replaced with space
				if (folderTitle.length() > 100) {
					folderTitle = folderTitle.substring(0, 100);
				}
				/*
				 * TODO: We have the right permissions?
				 */
				folderIdToUse = DocsAndMediaUtil.getOrCreateFolder(serviceContext, userId, repositoryId, newFolderId, folderTitle);
			} else {
				folderIdToUse = newFolderId;
			}
			
			Enumeration<?> ea = v.elements();
			String sourceFile = "";
			while (ea.hasMoreElements()) {
				EmbeddedObject eo = (EmbeddedObject)ea.nextElement();
				if (eo.getType() == EmbeddedObject.EMBED_ATTACHMENT) {
					sourceFile = eo.getSource();
					eo.extractFile(getTempDir() + sourceFile);
					
					numAttachments++;
					
					File newFile = new File(getTempDir() + sourceFile);
					/*
					 * Now saving attachment to Document and Media folder
					 */
					FileEntry fileEntry = DocsAndMediaUtil.uploadFileToFolder(userId, groupId, 
							folderIdToUse, newFile, sourceFile,
							dominoFieldNameWithTitle + CharPool.NEW_LINE + CharPool.NEW_LINE + documentDescription);

					/*
					 * Now extracting tags
					 */
					List<String> tagList = new ArrayList<String>();;
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
					/*
					 * Now extracting categories
					 */
					List<String> catList = new ArrayList<String>();
					if(extractCategories && Validator.isNotNull(assetVocabulary)) {
						try {
							
							ExtractCategories(userId, groupId, locale, doc, 
									dominoFieldNameWithCategories, assetVocabulary, serviceContext, catList);
						} catch (Exception e1) {
							_log.info(
									"Error extracting categories "
									+ "for doc with universalId = " + doc.getUniversalID());
							e1.printStackTrace();
						}					
					}
					/*
					 * If we have some tag or some category then we have to call UpdateAsset
					 */
					if (tagList.size() > 0 || catList.size() > 0) {
						
						//UpdateAsset needs a String array for tags and a Long array for categoryIds
						String [] tags = tagList.toArray(new String[tagList.size()]);
						
						long[] catsLong = new long[catList.size()];     
						for (int i = 0; i < catList.size(); i++) {     
							catsLong[i] = Long.parseLong(catList.get(i));     
						}  
						try {
							DocsAndMediaUtil.addTagsAndCategories(userId, fileEntry, catsLong, tags);
						} catch (Exception e1) {
							_log.info(
									"Error savings tags and categories in fileEntry "
									+ "for doc with universalId = " + doc.getUniversalID());
							e1.printStackTrace();
						}
					}
					if (_log.isDebugEnabled())
						_log.debug(fileEntry.getTitle());
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

	private static void ExtractCategories(long userId, long groupId, Locale locale, Document doc, 
			String dominoFieldNameWithCategories, AssetVocabulary assetVocabulary, ServiceContext serviceContext, List<String> returnedIds) 
			throws Exception {
		
		if(doc.hasItem(dominoFieldNameWithCategories)) {
			Item tagsNote = doc.getFirstItem(dominoFieldNameWithCategories);
			List<String> catsList = tagsNote.getValues();
				for (String cat : catsList) {
					System.out.print("cat " + cat);
					String []  separatedCats = cat.split("\\\\");
					List<String> separatedCatsList = new ArrayList<String>(Arrays.asList(separatedCats));
					DocsAndMediaUtil.getChildCategory(userId, groupId, locale, 
						separatedCatsList, 0, 0, 
						assetVocabulary.getVocabularyId(), serviceContext, returnedIds);
				}		
		}
	
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
	
	private static String toWord(String text) {
		if (Validator.isNull(text)) {
			return text;
		}

		char[] textCharArray = text.toCharArray();

		for (int i = 0; i < textCharArray.length; i++) {
			char c = textCharArray[i];

			for (char invalidChar : INVALID_CHARACTERS) {
				if (c == invalidChar) {
					textCharArray[i] = CharPool.SPACE;

					break;
				}
			}
		}

		return new String(textCharArray);
	}
	private static Log _log = LogFactoryUtil.getLog(DocsAndMediaUtil.class);

}
