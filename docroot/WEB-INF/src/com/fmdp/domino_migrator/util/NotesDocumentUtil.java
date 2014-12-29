package com.fmdp.domino_migrator.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Vector;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.SystemProperties;

import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;

public class NotesDocumentUtil {

	public static int ExtractAndSaveAttachment(long userId, long groupId, long newFolderId, Document doc, String fieldNameWithAttachment, 
			boolean extractTags, String fieldNameWithTags) 
					throws NotesException, PortalException, SystemException, FileNotFoundException {
		int numAttachments = 0;
		if(doc.isValid() && doc.hasItem(fieldNameWithAttachment)) {
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
					FileEntry fileEntry = DocsAndMediaUtil.uploadFileToFolder(userId, groupId, newFolderId, newFile, sourceFile);

					ExtractAndSaveTags(userId, doc, extractTags, fieldNameWithTags, fileEntry);
					
					System.out.print(fileEntry.getTitle() + StringPool.NEW_LINE);
				}
			}
		}
		return numAttachments;
	}

	private static void ExtractAndSaveTags(long userId, Document doc, boolean extractTags, String fieldNameWithTags, FileEntry fileEntry) 
			throws NotesException, PortalException, SystemException {

		if(extractTags && doc.hasItem(fieldNameWithTags)) {
			Item tagsNote = doc.getFirstItem(fieldNameWithTags);
			Vector<?> vi = tagsNote.getValues();
			String [] tags = vi.toArray(new String[vi.size()]);
			if (vi.size()>0) {
				DocsAndMediaUtil.addTags(userId, fileEntry, tags);
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

	public static Long DBColumn(String strClass, String strNoCache, String strServer , String strDatabase, 
			String strView, int numCol, Session s) throws NotesException {
		char quotes = CharPool.QUOTE;
		String strFormula = "@Left(@Trim( @DbColumn(" + quotes + strClass + quotes + ":" + quotes + strNoCache 
				+ quotes + ";" + quotes + strServer + quotes + ":" 
				+ quotes + strDatabase + quotes + ";" + quotes 
				+ strView + quotes + ";" + numCol + "));1)";
		Vector<?> v = s.evaluate(strFormula);
		long t = Long.valueOf(v.firstElement().toString());
		return t;
		
	}

}
