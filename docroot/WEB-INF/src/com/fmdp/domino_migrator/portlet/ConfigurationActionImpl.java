package com.fmdp.domino_migrator.portlet;



import lotus.domino.*;

import com.fmdp.domino_migrator.util.DominoProxyUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


public class ConfigurationActionImpl implements ConfigurationAction {
	
    public void processAction(PortletConfig portletConfig,
            ActionRequest actionRequest, ActionResponse actionResponse)
            throws Exception {
        
		String dominoHostName = ParamUtil.getString(
				actionRequest, "dominoHostName");
		String dominoUserName = ParamUtil.getString(
				actionRequest, "dominoUserName");
		String dominoUserPassword = ParamUtil.getString(
				actionRequest, "dominoUserPassword");

		if (Validator.isNull(dominoHostName)) {
			SessionErrors.add(actionRequest, "dominoServerNameRequired");
		}
		if (Validator.isNull(dominoUserName)) {
			SessionErrors.add(actionRequest, "dominoUserNameRequired");
		}
		if (Validator.isNull(dominoUserPassword)) {
			SessionErrors.add(actionRequest, "dominoUserPasswordRequired");
		}
		if (SessionErrors.isEmpty(actionRequest)) {

			DominoProxyUtil dominoProxy = DominoProxyUtil.getInstance();
			dominoProxy.openDominoSession(dominoHostName, dominoUserName, dominoUserPassword);
			if (!dominoProxy.isDominoSessionAvailable()) {
				SessionErrors.add(actionRequest, "noDominoSessionAvalaible");
				return;
			}
	        String portletResource = ParamUtil.getString(actionRequest,"portletResource");
	        PortletPreferences preferences = actionRequest.getPreferences();
	        preferences.setValue("dominoHostName", dominoHostName);
	        preferences.setValue("dominoUserName", dominoUserName);
	        preferences.setValue("dominoUserPassword", dominoUserPassword);
	        preferences.store();
	
	        dominoProxy.closeDominoSession();
	        
	        SessionMessages.add(actionRequest, "success");
	        SessionMessages.add(
	                actionRequest,
	                portletConfig.getPortletName() +
	                SessionMessages.KEY_SUFFIX_REFRESH_PORTLET,
	                portletResource);
		}

    }
 
    public String render(PortletConfig portletConfig,
            RenderRequest renderRequest, RenderResponse renderResponse)
            throws Exception {
 
        return "/configuration.jsp";
    }
	private static Log _log = LogFactoryUtil.getLog(ConfigurationActionImpl.class);
}
