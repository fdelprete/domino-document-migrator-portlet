package com.fmdp.domino_migrator.portlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.portlet.ActionRequest;
import javax.portlet.PortletException;


import javax.portlet.PortletPreferences;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import lotus.domino.Database;
import lotus.domino.View;
import lotus.domino.ACL;

import com.fmdp.bulk.domino.NotesAttachmentTaskExecutor;
import com.fmdp.domino_migrator.util.DominoProxyUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.BackgroundTask;
import com.liferay.portal.service.BackgroundTaskLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

public class DominoDocMigPortlet extends MVCPortlet {
	private static Log _log = LogFactoryUtil.getLog(DominoDocMigPortlet.class);

	/**
	 * @param actionRequest
	 * @param actionResponse
	 * @throws PortletException
	 * @throws IOException
	 */
	public void saveDominoConfig(javax.portlet.ActionRequest actionRequest,
			javax.portlet.ActionResponse actionResponse)
			throws PortletException, IOException, Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
        if (!cmd.equals(Constants.UPDATE)) {
            return;
        }
		boolean isValid = false;
		validateDominoParameters(actionRequest);
		if (SessionErrors.isEmpty(actionRequest)) {
			isValid = true;
		}
        
		String dominoHostName = ParamUtil.getString(
				actionRequest, "dominoHostName");
		String dominoUserName = ParamUtil.getString(
				actionRequest, "dominoUserName");
		String dominoUserPassword = ParamUtil.getString(
				actionRequest, "dominoUserPassword");
		String dominoDatabaseName = ParamUtil.getString(
				actionRequest, "dominoDatabaseName");
		String dominoViewName = ParamUtil.getString(
				actionRequest, "dominoViewName");
		String dominoFieldName = ParamUtil.getString(
				actionRequest, "dominoFieldName");
		String dominoFieldNameWithTags = ParamUtil.getString(
				actionRequest, "dominoFieldNameWithTags");
		boolean extractTags = ParamUtil.getBoolean(
				actionRequest, "extractTags");
		long newFolderId = ParamUtil.getLong(
				actionRequest, "newFolderId");

		if (_log.isDebugEnabled()) {
			_log.debug("saveDominoConfig - dominoHostName " + dominoHostName);
			_log.debug("saveDominoConfig - dominoUserName " + dominoUserName);
			_log.debug("saveDominoConfig - dominoUserPassword " + dominoUserPassword);
			
		}	     

			PortletPreferences preferences = actionRequest.getPreferences();
	        //PortletPreferences preferences = PortletPreferencesFactoryUtil.getPortletSetup(actionRequest, portletResource);
	        preferences.setValue("dominoHostName", dominoHostName);
	        preferences.setValue("dominoUserName", dominoUserName);
	        preferences.setValue("dominoUserPassword", dominoUserPassword);
	        preferences.setValue("dominoDatabaseName", dominoDatabaseName);
	        preferences.setValue("dominoViewName", dominoViewName);
	        preferences.setValue("dominoFieldName", dominoFieldName);
	        preferences.setValue("dominoFieldNameWithTags", dominoFieldNameWithTags);
	        preferences.setValue("extractTags", String.valueOf(extractTags));
	        preferences.setValue("newFolderId", String.valueOf(newFolderId));
	        preferences.setValue("isConfigValid", String.valueOf(isValid));
	        preferences.store();
			if (SessionErrors.isEmpty(actionRequest)) {	 
				SessionMessages.add(actionRequest, "success");
			}
	}
	
	protected void validateDominoParameters(ActionRequest actionRequest) 
			throws Exception {

		String dominoHostName = ParamUtil.getString(
				actionRequest, "dominoHostName");
		String dominoUserName = ParamUtil.getString(
				actionRequest, "dominoUserName");
		String dominoUserPassword = ParamUtil.getString(
				actionRequest, "dominoUserPassword");
		String dominoDatabaseName = ParamUtil.getString(
				actionRequest, "dominoDatabaseName");
		String dominoViewName = ParamUtil.getString(
				actionRequest, "dominoViewName");
		String dominoFieldName = ParamUtil.getString(
				actionRequest, "dominoFieldName");
		String dominoFieldNameWithTags = ParamUtil.getString(
				actionRequest, "dominoFieldNameWithTags");
		boolean extractTags = ParamUtil.getBoolean(
				actionRequest, "extractTags");

		if (Validator.isNull(dominoHostName)) {
			SessionErrors.add(actionRequest, "dominoServerNameRequired");
		}
		if (Validator.isNull(dominoUserName)) {
			SessionErrors.add(actionRequest, "dominoUserNameRequired");
		}
		if (Validator.isNull(dominoUserPassword)) {
			SessionErrors.add(actionRequest, "dominoUserPasswordRequired");
		}
		if (Validator.isNull(dominoDatabaseName)) {
			SessionErrors.add(actionRequest, "dominoDatabaseNameRequired");
		}
		if (Validator.isNull(dominoViewName)) {
			SessionErrors.add(actionRequest, "dominoViewNameRequired");
		}
		if (Validator.isNull(dominoFieldName)) {
			SessionErrors.add(actionRequest, "dominoFieldNameRequired");
		}
		if (extractTags && Validator.isNull(dominoFieldNameWithTags)) {
			SessionErrors.add(actionRequest, "dominoFieldNameWithTagsRequired");
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			DominoProxyUtil dominoProxy = DominoProxyUtil.getInstance();
			dominoProxy.openDominoSession(dominoHostName, dominoUserName, dominoUserPassword);
			if (!dominoProxy.isDominoSessionAvailable()) {
				SessionErrors.add(actionRequest, "noDominoSessionAvalaible");
				return;
			}
			String server = dominoProxy.dominoSession.getServerName();
			Database db = dominoProxy.dominoSession.getDatabase(server, dominoDatabaseName);
			if (!db.isOpen()) {
				SessionErrors.add(actionRequest, "dominoDatabaseUnavalaible");
				return;
			}
			ACL acl = db.getACL();
			Vector<?>  roles = acl.getRoles();
		    String theRoles = StringUtil.merge(roles);
		    PortletPreferences preferences = actionRequest.getPreferences();
		    preferences.setValue("dominoDatabaseAcl", theRoles);
		    preferences.store();
		    
			View view = db.getView(dominoViewName);
			if (view == null ) {
				SessionErrors.add(actionRequest, "dominoViewUnavalaible");
				return;
			}
			dominoProxy.closeDominoSession();
		}
		
	}
	public void startTask(javax.portlet.ActionRequest actionRequest,
			javax.portlet.ActionResponse actionResponse)
			throws PortletException, IOException {
        String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

        if (!cmd.equals(Constants.UPDATE)) {
            return;
        }
        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        String portletId = PortalUtil.getPortletId(actionRequest);
        ServiceContext serviceContext = null;
		try {
			serviceContext = ServiceContextFactory.getInstance(actionRequest);
		} catch (PortalException e) {
			e.printStackTrace();
			SessionErrors.add(
					actionRequest, "errorGettingServiceContext");
			return;
		} catch (SystemException e) {
			e.printStackTrace();
			SessionErrors.add(
					actionRequest, "errorGettingServiceContext");
			return;
		}
		
		PortletPreferences preferences = actionRequest.getPreferences();
		
		String dominoHostName = preferences.getValue("dominoHostName", StringPool.BLANK);
		String dominoUserName = preferences.getValue("dominoUserName", StringPool.BLANK);
		String dominoUserPassword = preferences.getValue("dominoUserPassword", StringPool.BLANK);
		String dominoDatabaseName = preferences.getValue("dominoDatabaseName", StringPool.BLANK);
		String dominoViewName = preferences.getValue("dominoViewName", StringPool.BLANK);
		String dominoFieldName = preferences.getValue("dominoFieldName", StringPool.BLANK);
		String dominoFieldNameWithTags = preferences.getValue("dominoFieldNameWithTags", StringPool.BLANK);
		boolean extractTags = GetterUtil.getBoolean(preferences.getValue("extractTags", StringPool.BLANK));
		
		long newFolderId = GetterUtil.getLong(preferences.getValue("newFolderId", StringPool.BLANK));
		
		HttpServletRequest request = PortalUtil.getHttpServletRequest(actionRequest);
		ServletContext servletContext = request.getSession().getServletContext();
		
		String[] servletContextNames = new String[1];
		servletContextNames[0] = servletContext.getServletContextName();

		Map<String, Serializable> taskContextMap = new HashMap<String, Serializable>();

		taskContextMap.put("portletId", portletId);
		taskContextMap.put("dominoHostName", dominoHostName);
		taskContextMap.put("dominoUserName", dominoUserName);
		taskContextMap.put("dominoUserPassword", dominoUserPassword);
		taskContextMap.put("dominoDatabaseName", dominoDatabaseName);
		taskContextMap.put("dominoViewName", dominoViewName);
		taskContextMap.put("dominoFieldName", dominoFieldName);
		taskContextMap.put("dominoFieldNameWithTags", dominoFieldNameWithTags);
		taskContextMap.put("extractTags", extractTags);
		taskContextMap.put("newFolderId", newFolderId);	
		taskContextMap.put("groupId", themeDisplay.getScopeGroupId());
		taskContextMap.put("userId", themeDisplay.getUserId());
		
        try {
        	BackgroundTask backgroundTask = BackgroundTaskLocalServiceUtil.addBackgroundTask(themeDisplay.getUserId(), themeDisplay.getSiteGroupId(), 
					StringPool.BLANK, servletContextNames, NotesAttachmentTaskExecutor.class, 
					taskContextMap, serviceContext);
        	actionRequest.setAttribute("backgroundTaskId", backgroundTask.getBackgroundTaskId());
		} catch (PortalException e) {
			e.printStackTrace();
			SessionErrors.add(
					actionRequest, "errorStartingBackgroundTask");
			return;
		} catch (SystemException e) {
			e.printStackTrace();
			SessionErrors.add(
					actionRequest, "errorStartingBackgroundTask");
			return;
		}

	 
	        SessionMessages.add(actionRequest, "success");

	}

}
