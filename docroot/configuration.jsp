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
			<aui:input cssClass="lfr-input-text-container" helpMessage="the-domino-host-name-is" label="domino-host-name" name="dominoHostName" type="text" value="<%= dominoHostName %>" />
	
			<aui:input cssClass="lfr-input-text-container" helpMessage="the-domino-user-name-is" label="username" name='<%= "dominoUserName" %>' type="text" value="<%= dominoUserName %>" />
	
			<aui:input cssClass="lfr-input-text-container" label="password" name='<%= "dominoUserPassword" %>' type="password" value="<%= dominoUserPassword %>" />
	
			<aui:button-row>
	
				<%
				String taglibOnClick = renderResponse.getNamespace() + "testSettings('dominoConnection');";
				%>
	
				<aui:button onClick="<%= taglibOnClick %>" value="test-domino-connection" />
			</aui:button-row>
	</aui:fieldset>
	
	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>	
</aui:form>


<aui:script>

	Liferay.provide(
		window,
		'<portlet:namespace />testSettings',
		function(type) {
			var A = AUI();

			var url = null;

			var data = {};

			if (type == "dominoConnection") {
				url = "<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="test_domino_connection" /></liferay-portlet:renderURL>";
			}
			else if (type == "ldapGroups") {
			}
			else if (type == "ldapUsers") {
			}

			if (url != null) {
				data.<portlet:namespace />dominoHostName = document.<portlet:namespace />fm['<portlet:namespace />dominoHostName'].value;
				data.<portlet:namespace />dominoUserName = document.<portlet:namespace />fm['<portlet:namespace />dominoUserName'].value;
				data.<portlet:namespace />dominoUserPassword = document.<portlet:namespace />fm['<portlet:namespace />dominoUserPassword'].value;

				var dialog = Liferay.Util.Window.getWindow(
					{
						dialog: {
							destroyOnHide: true
						},
						title: '<%= UnicodeLanguageUtil.get(pageContext, "domino-test") %>'
					}
				);

				dialog.plug(
					A.Plugin.IO,
					{
						data: data,
						uri: url
					}
				);
			}
		},
		['aui-io-plugin-deprecated', 'aui-io', 'liferay-util-window']
	);


</aui:script>