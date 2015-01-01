package com.fmdp.domino_migrator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;

public class DocsAndMediaUtil {
	private static Log _log = LogFactoryUtil.getLog(DocsAndMediaUtil.class);
	
	/**
	 * @param userId
	 * @param groupId
	 * @param folderId
	 * @param file
	 * @param sourceFileName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 * @throws FileNotFoundException
	 */
	public static FileEntry uploadFileToFolder(long userId, long groupId, 
			long folderId, File file, String sourceFileName) throws PortalException, SystemException, FileNotFoundException {
		
		String contentType = MimeTypesUtil.getContentType(file);
        
		Folder folderEntry = null;
        
        FileEntry returnedFE = null;
		
        folderEntry = DLAppLocalServiceUtil.getFolder(folderId);
		if (folderEntry == null) {
			_log.error("Error getting folder entry");
			return returnedFE;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("DM Folder: " + folderEntry.getName());
		}			            
        
		InputStream inputStream = null;
		inputStream = new FileInputStream(file);
        long repositoryId = folderEntry.getRepositoryId();
    	String selectedFileName = sourceFileName;
		while (true) {
			try {
				DLAppLocalServiceUtil.getFileEntry(
					groupId, folderId,
					selectedFileName);

				StringBundler sb = new StringBundler(5);

				sb.append(FileUtil.stripExtension(selectedFileName));
				sb.append(StringPool.DASH);
				sb.append(StringUtil.randomString());
				sb.append(StringPool.PERIOD);
				sb.append(FileUtil.getExtension(selectedFileName));

				selectedFileName = sb.toString();
			}
			catch (Exception e) {
				break;
			}
		}
		ServiceContext serviceContext = new ServiceContext();
		serviceContext.setScopeGroupId(groupId);
		
        FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(userId, 
                repositoryId, 
                folderId, 
                selectedFileName, //file.getName(), 
                contentType, 
                selectedFileName, 
                "", 
                "", 
                inputStream, 
                file.length(), 
                serviceContext);
       
		if (_log.isDebugEnabled()) {
			_log.debug("DM file uploaded: " + fileEntry.getTitle());
		}
		return fileEntry;
	}
	/**
	 * @param userId
	 * @param fe
	 * @param tags
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static void addTags(long userId, FileEntry fe, String[] tags) throws PortalException, SystemException {
		DLAppLocalServiceUtil.updateAsset(userId, fe, fe.getLatestFileVersion(), null, tags, null);
	}
}
