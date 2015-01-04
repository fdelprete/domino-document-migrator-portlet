package com.fmdp.domino_migrator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;



import java.util.Map;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
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
import com.liferay.portlet.asset.AssetTagException;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.model.AssetVocabulary;
import com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetVocabularyLocalServiceUtil;
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
			long folderId, File file, String sourceFileName, String documentDescription) throws PortalException, SystemException, FileNotFoundException {
		
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
                documentDescription, 
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
	public static void addTags(long userId, FileEntry fe, long [] catIds, String[] tags) throws Exception {
		try {
			DLAppLocalServiceUtil.updateAsset(userId, fe, fe.getLatestFileVersion(), catIds, tags, null);
		} catch (Exception e) {
			if (e instanceof AssetTagException) {
				_log.info("tags error: " + tags.toString() + " - Excpetion message: " + e.getMessage());
				
			}
			else {
				throw e;
			}
		}
	}

	public static AssetVocabulary createVocabulary(long userId, long groupId, Locale locale, String vocabularyName){
		ServiceContext serviceContext = new ServiceContext();
		serviceContext.setScopeGroupId(groupId);
		Map<Locale, String> titleMap = new HashMap<Locale, String>();
		titleMap.put(locale, vocabularyName);
		Map<Locale, String> descriptionMap = new HashMap<Locale, String>();
		descriptionMap.put(locale, vocabularyName);
		AssetVocabulary createdVocabulary = null;
		try {
			createdVocabulary = AssetVocabularyLocalServiceUtil.addVocabulary(userId, vocabularyName, 
					titleMap, descriptionMap, null, serviceContext);
			_log.info("Import :  new vocabulary created :" + vocabularyName + " : category_id : " + createdVocabulary.getVocabularyId());
		} catch (PortalException e) {
			try {
				createdVocabulary = AssetVocabularyLocalServiceUtil.getGroupVocabulary(groupId, vocabularyName);
				_log.info("Import : vocabulary alredy exist :" + vocabularyName + " : category_id : " + createdVocabulary.getVocabularyId());
			} catch (PortalException e1) {
				_log.error("Errore wihle get vocabulary with name : " + vocabularyName + "  :: " +  e.getMessage(), e);
			} catch (SystemException e1) {
				_log.error("Errore wihle get vocabulary with name : " + vocabularyName + "  :: " +  e.getMessage(), e);
			}
		} catch (SystemException e) {
			_log.error("Errore wihle create vocabulary with name : " + vocabularyName + "  :: " +  e.getMessage(), e);
		}
		return createdVocabulary;
	}
	
	public static List<String> getChildCategory(long userId, long groupId, Locale locale, List<String> cat, int element, long parentCategoryId, 
			long vocabularyId, ServiceContext serviceContext){
		List<String> catIds = new ArrayList<String>();
		Map<Locale, String> titleMap = new HashMap<Locale, String>();
		titleMap.put(locale, cat.get(element));
		Map<Locale, String> descriptionMap = new HashMap<Locale, String>();
		descriptionMap.put(locale, cat.get(element));
		AssetCategory createdCategory = null;
		try {
			createdCategory = AssetCategoryLocalServiceUtil.addCategory(userId, parentCategoryId, 
					titleMap, descriptionMap, vocabularyId, null, serviceContext);
			_log.info("Import :  new category created :" + cat.get(element) + " : category_id : " + createdCategory.getCategoryId());
		} catch (PortalException e) {
			try {
				
				DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(AssetCategory.class);
				dynamicQuery.add(PropertyFactoryUtil.forName("name").eq(cat.get(element)));
				List<AssetCategory> createdCategoryList = AssetCategoryLocalServiceUtil.dynamicQuery(dynamicQuery);
				for(AssetCategory catt : createdCategoryList){
					if(catt.getParentCategoryId()==parentCategoryId && catt.getGroupId() == groupId){
						createdCategory = catt;
						_log.info("Import : category alredy exist :" + cat.get(element) + " : category_id : " + createdCategory.getCategoryId());
						break;
					}
				}
			} catch (SystemException e1) {
				_log.error("Errore wihle search category vocabulary with name : " + cat.get(element) + "  :: " +  e1.getMessage(), e1);
			}
		} catch (SystemException e) {
			_log.error("Errore wihle create category with name : " + cat.get(element) + "  :: " +  e.getMessage(), e);
		}
		if (element < cat.size() && !cat.get(element + 1).isEmpty()){
				getChildCategory(userId, groupId, locale, cat, element + 1, createdCategory.getCategoryId(), vocabularyId, serviceContext);
		}
		catIds.add(Long.toString(createdCategory.getCategoryId()));
		return catIds;
	}
}


