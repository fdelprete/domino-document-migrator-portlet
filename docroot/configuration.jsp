<%@ include file="/init.jsp"%>
<%

PortletPreferences preferences = renderRequest.getPreferences();
 
String dominoHostName = portletPreferences.getValue("dominoHostName", StringPool.BLANK);
String dominoUserName = portletPreferences.getValue("dominoUserName", StringPool.BLANK);
String dominoUserPassword = portletPreferences.getValue("dominoUserPassword", StringPool.BLANK);

%>

<liferay-portlet:actionURL var="configurationURL"
	portletConfiguration="true" />
<liferay-portlet:renderURL portletConfiguration="true"
	var="configurationRenderURL">
</liferay-portlet:renderURL>
<aui:form method="post" action="<%=configurationURL.toString()%>"
	name="fm">
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />
	<liferay-ui:error key="dominoServerNameRequired"
		message="please-enter-the-domino-server-name" />
	<liferay-ui:error key="dominoUserNameRequired"
		message="please-enter-the-domino-user-name" />
	<liferay-ui:error key="dominoUserPasswordRequired"
		message="please-enter-the-password-for-the-domino-user-name" />
	<liferay-ui:error key="noDominoSessionAvalaible"
		message="no-domino-session-avaliable" />

		<aui:fieldset>
			<aui:input cssClass="lfr-input-text-container" 
				helpMessage="the-domino-host-name-is" 
				label="domino-host-name" 
				name="dominoHostName" 
				type="text" 
				value="<%= dominoHostName %>" />
	
			<aui:input cssClass="lfr-input-text-container" 
				helpMessage="the-domino-user-name-is" 
				label="username" 
				name='<%= "dominoUserName" %>' 
				type="text" 
				value="<%= dominoUserName %>" />
	
			<aui:input cssClass="lfr-input-text-container" 
				label="password" 
				name='<%= "dominoUserPassword" %>' 
				type="password" 
				value="<%= dominoUserPassword %>" />
	
	</aui:fieldset>
	
	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>	
</aui:form>
