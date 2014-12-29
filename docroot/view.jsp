<%@ include file="/init.jsp"%>
<%
	String redirect = ParamUtil.getString(request, "redirect");
	String backURL = ParamUtil.getString(request, "backURL", redirect);
	PortletPreferences preferences = renderRequest.getPreferences();

	Long backgroundTaskId = ParamUtil.getLong(request,
			"backgroundtaskId");
	// String portletResource = ParamUtil.getString(request, "portletResource");

	String dominoHostName = portletPreferences.getValue(
			"dominoHostName", StringPool.BLANK);
	String dominoUserName = portletPreferences.getValue(
			"dominoUserName", StringPool.BLANK);
	String dominoUserPassword = portletPreferences.getValue(
			"dominoUserPassword", StringPool.BLANK);
	String dominoDatabaseName = portletPreferences.getValue(
			"dominoDatabaseName", StringPool.BLANK);
	String dominoViewName = portletPreferences.getValue(
			"dominoViewName", StringPool.BLANK);
	String dominoFieldName = portletPreferences.getValue(
			"dominoFieldName", StringPool.BLANK);
	String dominoFieldNameWithTags = portletPreferences.getValue(
			"dominoFieldNameWithTags", StringPool.BLANK);
	String dominoDatabaseAcl = portletPreferences.getValue(
			"dominoDatabaseAcl", StringPool.BLANK);
	boolean isConfigValid = GetterUtil.getBoolean(portletPreferences
			.getValue("isConfigValid", StringPool.BLANK));
	boolean extractTags = GetterUtil.getBoolean(portletPreferences
			.getValue("extractTags", StringPool.BLANK));

	long newFolderId = GetterUtil.getLong(portletPreferences.getValue(
			"newFolderId", StringPool.BLANK));

	String folderName = StringPool.BLANK;
	Folder folder = null;
	if (newFolderId > 0) {
		folder = DLAppLocalServiceUtil.getFolder(newFolderId);

		folder = folder.toEscapedModel();

		folderName = folder.getName();
	} else {
		folderName = LanguageUtil.get(pageContext, "home");
	}
	String modelResource = null;
	String modelResourceDescription = null;
	String resourcePrimKey = null;

	if (folder != null) {
		modelResource = DLFolderConstants.getClassName();
		modelResourceDescription = folder.getName();
		resourcePrimKey = String.valueOf(newFolderId);
	} else {
		modelResource = "com.liferay.portlet.documentlibrary";
		modelResourceDescription = themeDisplay.getScopeGroupName();
		resourcePrimKey = String.valueOf(scopeGroupId);
	}
%>
<liferay-ui:tabs names="new-import-process,current-and-previous"
	param="tabs2" refresh="<%=false%>">
	<liferay-ui:section>
		<portlet:actionURL var="dominoDocMigURL">
			<portlet:param name="<%=ActionRequest.ACTION_NAME%>"
				value="saveDominoConfig" />
		</portlet:actionURL>

		<aui:form method="post" action="<%=dominoDocMigURL.toString()%>"
			name="fm">
			<aui:input name="<%=Constants.CMD%>" type="hidden"
				value="<%=Constants.UPDATE%>" />
			<liferay-ui:success key="success"
				message="the-domino-configuration-was-saved-and-seems-to-be-valid" />

			<liferay-ui:error key="noDominoSessionAvalaible"
				message="no-domino-session-avaliable" />
			<liferay-ui:error key="dominoDatabaseUnavalaible"
				message="the-database-does-not-exist-on-server" />
			<liferay-ui:error key="dominoViewUnavalaible"
				message="the-specified-view-does-not-exist-on-database" />

			<liferay-ui:panel-container extended="<%=Boolean.TRUE%>"
				id="webFormConfiguration" persistState="<%=true%>">
				<liferay-ui:panel collapsible="<%=true%>" extended="<%=true%>"
					id="domImportServer" persistState="<%=true%>"
					title="domino-server-parameters">
					<liferay-ui:error key="dominoServerNameRequired"
						message="please-enter-the-domino-server-name" />
					<liferay-ui:error key="dominoUserNameRequired"
						message="please-enter-the-domino-user-name" />
					<liferay-ui:error key="dominoUserPasswordRequired"
						message="please-enter-the-password-for-the-domino-user-name" />

					<aui:fieldset>
						<aui:input cssClass="lfr-input-text-container"
							helpMessage="the-domino-host-name-is" label="domino-host-name"
							name="dominoHostName" type="text" value="<%=dominoHostName%>" />

						<aui:input cssClass="lfr-input-text-container"
							helpMessage="the-domino-user-name-is" label="username"
							name='<%="dominoUserName"%>' type="text"
							value="<%=dominoUserName%>" />

						<aui:input cssClass="lfr-input-text-container" label="password"
							name='<%="dominoUserPassword"%>' type="password"
							value="<%=dominoUserPassword%>" />

						<aui:button-row>

							<%
								String taglibOnClick = renderResponse
																	.getNamespace()
																	+ "testSettings('dominoConnection');";
							%>

							<aui:button onClick="<%=taglibOnClick%>"
								value="test-domino-connection" />
						</aui:button-row>
					</aui:fieldset>
				</liferay-ui:panel>
				<liferay-ui:panel collapsible="<%=true%>" extended="<%=true%>"
					id="domImportDatabase" persistState="<%=true%>"
					title="domino-database">
					<liferay-ui:error key="dominoDatabaseNameRequired"
						message="please-enter-the-name-for-the-domino-database" />
					<liferay-ui:error key="dominoViewNameRequired"
						message="please-enter-the-view-name" />
					<liferay-ui:error key="dominoFieldNameRequired"
						message="please-enter-the-rich-text-field-name" />
					<aui:fieldset>
						<aui:input cssClass="lfr-input-text-container"
							helpMessage="the-domino-database-path-is"
							label="domino-database-name" name="dominoDatabaseName"
							type="text" value="<%=dominoDatabaseName%>" />

						<aui:input cssClass="lfr-input-text-container"
							helpMessage="the-view-name-is" label="doimino-view-name"
							name="dominoViewName" type="text" value="<%=dominoViewName%>" />

						<aui:input cssClass="lfr-input-text-container"
							helpMessage="the-domino-field-name-is" label="domino-field-name"
							name="dominoFieldName" type="text" value="<%=dominoFieldName%>" />

						<aui:button-row>

							<%
								String taglibOnClick = renderResponse
																	.getNamespace()
																	+ "testSettings('dominoDatabase');";
							%>

							<aui:button onClick="<%=taglibOnClick%>"
								value="test-domino-database" />
						</aui:button-row>
					</aui:fieldset>
				</liferay-ui:panel>
				<liferay-ui:panel collapsible="<%=true%>" extended="<%=true%>"
					id="domImportDocMedia" persistState="<%=true%>"
					title="documents-and-media">
					<aui:fieldset>
						<aui:field-wrapper label="select-folder">
							<div class="input-append">
								<liferay-ui:input-resource id="folderName" url="<%=folderName%>" />
								<aui:button name="selectFolderButton" id="selectFolderButton"
									value="select" disabled="<%=false%>" />
								<liferay-security:permissionsURL
									modelResource="<%=modelResource%>"
									modelResourceDescription="<%=HtmlUtil
												.escape(modelResourceDescription)%>"
									resourcePrimKey="<%=resourcePrimKey%>" var="permissionsURL"
									windowState="<%=LiferayWindowState.POP_UP
												.toString()%>" />

								<liferay-ui:icon image="permissions" method="get"
									url="<%=permissionsURL%>" useDialog="<%=true%>"
									label="permissions" />

							</div>
						</aui:field-wrapper>
						<aui:input name="newFolderId" type="hidden"
							value="<%=newFolderId%>" />
					</aui:fieldset>
				</liferay-ui:panel>
				<liferay-ui:panel collapsible="<%=true%>" extended="<%=true%>"
					id="domExtractCatAndTag" persistState="<%=true%>"
					title="categories-and-tags-extraction">
					<liferay-ui:error key="dominoFieldNameWithTagsRequired"
						message="please-enter-the-field-name-containing-the-tags-for-the-notes-document" />

					<aui:input label="extract-tags" name="extractTags" type="checkbox"
						value="<%=extractTags%>" />
					<aui:input cssClass="lfr-input-text-container"
						label="domino-field-with-tags" name="dominoFieldNameWithTags"
						value="<%=dominoFieldNameWithTags%>" />
				</liferay-ui:panel>
				<liferay-ui:panel collapsible="<%=true%>" extended="<%=true%>"
					id="domImportTask" persistState="<%=true%>" title="task-execution">

					<aui:button-row>
						<liferay-portlet:actionURL var="taskURL">
							<portlet:param name="<%=ActionRequest.ACTION_NAME%>"
								value="startTask" />
							<portlet:param name="redirect" value="<%=currentURL%>" />
						</liferay-portlet:actionURL>

						<%
							String taglibTask = "submitForm(document."
															+ renderResponse.getNamespace()
															+ "fm, '" + taskURL + "');";
						%>
						<c:if test="<%=!isConfigValid%>">
							<div class="alert alert-info">
								<liferay-ui:message
									key="please-validate-and-save-the-config-before-starting-tha-background-task" />
							</div>
						</c:if>
						<aui:button onClick="<%=taglibTask%>" value="start-task"
							disabled="<%=!isConfigValid%>" />
					</aui:button-row>
				</liferay-ui:panel>
			</liferay-ui:panel-container>
			<aui:button-row>
				<aui:button type="submit" value="validate-and-save" />
			</aui:button-row>

			<liferay-portlet:renderURL
				portletName="<%=PortletKeys.DOCUMENT_LIBRARY%>"
				var="selectFolderURL"
				windowState="<%=LiferayWindowState.POP_UP.toString()%>">
				<portlet:param name="struts_action"
					value='<%="/document_library/select_folder"%>' />
			</liferay-portlet:renderURL>
			<aui:script use="aui-base">
	A.one('#<portlet:namespace />selectFolderButton').on(
		'click',
		function(event) {
			Liferay.Util.selectEntity(
				{
					dialog: {
						constrain: true,
						modal: true,
						width: 680
					},
					id: '_<%=PortletKeys.DOCUMENT_LIBRARY%>_selectFolder',
					title: '<liferay-ui:message arguments="folder" key="select-x" />',
					uri: '<%=selectFolderURL.toString()%>'
				},
				function(event) {
					var folderData = {
						idString: 'newFolderId',
						idValue: event.folderid,
						nameString: 'folderName',
						nameValue: event.foldername
					};
					Liferay.Util.selectFolder(folderData, '<portlet:namespace />');
				}
			);
		}
	);
			</aui:script>


		</aui:form>
	</liferay-ui:section>
	<liferay-ui:section>
		<div class="process-list" id="<portlet:namespace />importProcesses">
			<liferay-util:include page="/import_domino_processes.jsp"
				servletContext="<%=application%>" />
		</div>
	</liferay-ui:section>
</liferay-ui:tabs>
<portlet:renderURL var="testDominoUrl"
	windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>">
	<portlet:param name="mvcPath" value="/test_domino_connection.jsp" />
</portlet:renderURL>
<portlet:renderURL var="testDominoDbUrl"
	windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>">
	<portlet:param name="mvcPath" value="/test_domino_database.jsp" />
</portlet:renderURL>

<aui:script>

	Liferay.provide(
		window,
		'<portlet:namespace />testSettings',
		function(type) {
			var A = AUI();

			var url = null;

			var data = {};

			if (type == "dominoConnection") {
				url = "<%=testDominoUrl%>";
			}
			else if (type == "dominoDatabase") {
				url = "<%=testDominoDbUrl%>";
				data.<portlet:namespace />dominoDatabaseName = document.<portlet:namespace />fm['<portlet:namespace />dominoDatabaseName'].value;
				data.<portlet:namespace />dominoViewName = document.<portlet:namespace />fm['<portlet:namespace />dominoViewName'].value;
				data.<portlet:namespace />dominoFieldName = document.<portlet:namespace />fm['<portlet:namespace />dominoFieldName'].value;
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
						title: '<%=UnicodeLanguageUtil.get(pageContext, "domino-test")%>'
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