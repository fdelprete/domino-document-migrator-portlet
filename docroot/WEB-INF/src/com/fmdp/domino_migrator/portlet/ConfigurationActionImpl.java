package com.fmdp.domino_migrator.portlet;



import lotus.domino.*;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


public class ConfigurationActionImpl implements ConfigurationAction {
	private static Log _log = LogFactoryUtil.getLog(ConfigurationActionImpl.class);
	
    public void processAction(PortletConfig portletConfig,
            ActionRequest actionRequest, ActionResponse actionResponse)
            throws Exception {
        System.out.println("Start");		
 
        String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
        System.out.println("cmd: " + cmd);
        if (!cmd.equals(Constants.UPDATE)) {
            return;
        }

        System.out.println("Config");
        
		String dominoHostName = ParamUtil.getString(
				actionRequest, "dominoHostName");
		String dominoUserName = ParamUtil.getString(
				actionRequest, "dominoUserName");
		String dominoUserPassword = ParamUtil.getString(
				actionRequest, "dominoUserPassword");
		System.out.println("dominoHostName " + dominoHostName);
		System.out.println("dominoUserName " + dominoUserName);
		System.out.println("dominoUserPassword " + dominoUserPassword);
		try {
        Session s = NotesFactory.createSession(
        		dominoHostName, dominoUserName, dominoUserPassword);
        String p = s.getPlatform();
        System.out.println("Platform = " + p);		
        String commonUserName = s.getCommonUserName();
        System.out.println("commonUserName " + commonUserName);
		} catch (NotesException e2) {
			e2.printStackTrace ();
		}
	        String portletResource = ParamUtil.getString(actionRequest,"portletResource");
	        PortletPreferences preferences = actionRequest.getPreferences();
	        //PortletPreferences preferences = PortletPreferencesFactoryUtil.getPortletSetup(actionRequest, portletResource);
	        preferences.setValue("dominoHostName", dominoHostName);
	        preferences.setValue("dominoUserName", dominoUserName);
	        preferences.setValue("dominoUserPassword", dominoUserPassword);
	        preferences.store();
	 
	        SessionMessages.add(actionRequest, "success");
	        SessionMessages.add(
	                actionRequest,
	                portletConfig.getPortletName() +
	                SessionMessages.KEY_SUFFIX_REFRESH_PORTLET,
	                portletResource);        
    }
 
    public String render(PortletConfig portletConfig,
            RenderRequest renderRequest, RenderResponse renderResponse)
            throws Exception {
 
        return "/configuration.jsp";
    }
}
