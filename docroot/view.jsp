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
	
	if (Validator.isNull(dominoHostName) ||
			Validator.isNull(dominoUserName) ||
			Validator.isNull(dominoUserPassword)) {
	%>
		<liferay-ui:message key="please-define-each-of-the-domino-properties-database-name,-view-name-and-field-name" />

	<%
		return;
	}
			
	String dominoDatabaseName = portletPreferences.getValue(
			"dominoDatabaseName", StringPool.BLANK);
	String dominoViewName = portletPreferences.getValue(
			"dominoViewName", StringPool.BLANK);
	String dominoFieldName = portletPreferences.getValue(
			"dominoFieldName", StringPool.BLANK);
	String dominoFieldNameWithTags = portletPreferences.getValue(
			"dominoFieldNameWithTags", StringPool.BLANK);
	String dominoFieldNameWithCategories = portletPreferences.getValue(
			"dominoFieldNameWithCategories", StringPool.BLANK);
	String dominoDatabaseAcl = portletPreferences.getValue(
			"dominoDatabaseAcl", StringPool.BLANK);
	String dominoFieldNameWithDescr = portletPreferences.getValue(
			"dominoFieldNameWithDescr", StringPool.BLANK);
	String dominoFieldNameWithTitle = portletPreferences.getValue(
			"dominoFieldNameWithTitle", StringPool.BLANK);
	String vocabularyName = portletPreferences.getValue(
			"vocabularyName", StringPool.BLANK);

	boolean isConfigValid = GetterUtil.getBoolean(portletPreferences
			.getValue("isConfigValid", StringPool.BLANK));
	boolean extractTags = GetterUtil.getBoolean(portletPreferences
			.getValue("extractTags", StringPool.BLANK));
	boolean extractCategories = GetterUtil.getBoolean(portletPreferences
			.getValue("extractCategories", StringPool.BLANK));
	
	boolean extractDescription = GetterUtil.getBoolean(portletPreferences
			.getValue("extractDescription", StringPool.BLANK));
	
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
			<liferay-ui:success key="successTaskStarted"
				message="the-import-task-has-been-started" />

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
					<div class="alert">
						<liferay-ui:message key="change-connection-parameters-in-the-configuration-page" />
					</div>
					<dl class="editing-disabled">
						<dt>
							<liferay-ui:message key="domino-host-name" />
						</dt>
						<dd>
							<%=dominoHostName%>
						</dd>
						<dt>
							<liferay-ui:message key="username" />
						</dt>
						<dd>
							<%=dominoUserName%>
						</dd>
						<dt>
							<liferay-ui:message key="password" />
						</dt>
						<dd>
							<%="****"%>
						</dd>

					</dl>
					<aui:button-row>

						<%
							String taglibOnClick = renderResponse
																.getNamespace()
																+ "testSettings('dominoConnection');";
						%>
						<aui:button onClick="<%=taglibOnClick%>"
							value="test-domino-connection" />
					</aui:button-row>

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
							helpMessage="the-view-name-is" label="domino-view-name"
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
							</div>
							<liferay-ui:icon-menu>
								<liferay-security:permissionsURL
									modelResource="<%=modelResource%>"
									modelResourceDescription="<%=HtmlUtil.escape(modelResourceDescription)%>"
									resourcePrimKey="<%=resourcePrimKey%>" var="permissionsURL"
									windowState="<%=LiferayWindowState.POP_UP.toString()%>" />
								<liferay-ui:icon image="permissions" method="get"
									url="<%=permissionsURL%>" useDialog="<%=true%>"
									label="permissions" message="edit-permissions"/>
							</liferay-ui:icon-menu>

						</aui:field-wrapper>
						<aui:input name="newFolderId" type="hidden"
							value="<%=newFolderId%>" />
					</aui:fieldset>
				</liferay-ui:panel>
				<liferay-ui:panel collapsible="<%=true%>" extended="<%=true%>"
					id="domExtractDescription" persistState="<%=true%>"
					title="description-and-title-extraction">
					<liferay-ui:error key="dominoFieldNameWithDescrRequired"
						message="please-enter-the-field-name-containing-the-description-of-the-notes-document" />
					<liferay-ui:error key="dominoFieldNameWithTitleRequired"
						message="please-enter-the-field-name-containing-the-title-of-the-notes-document" />
						
					<aui:input cssClass="lfr-input-text-container"
						label="domino-field-with-title" name="dominoFieldNameWithTitle"
						value="<%=dominoFieldNameWithTitle%>" />

					<aui:input label="extract-description" name="extractDescription" type="checkbox"
						value="<%=extractDescription%>" />
					<aui:input cssClass="lfr-input-text-container"
						label="domino-field-with-description" name="dominoFieldNameWithDescr"
						value="<%=dominoFieldNameWithDescr%>" />
				</liferay-ui:panel>

				<liferay-ui:panel collapsible="<%=true%>" extended="<%=true%>"
					id="domExtractCatAndTag" persistState="<%=true%>"
					title="categories-and-tags-extraction">
					<liferay-ui:error key="dominoFieldNameWithTagsRequired"
						message="please-enter-the-field-name-containing-the-tags-for-the-notes-document" />
					<liferay-ui:error key="dominoFieldNameWithCategoriesRequired"
						message="please-enter-the-field-name-containing-the-categories-for-the-notes-document" />
					<liferay-ui:error key="vocabularyNameRequired"
						message="please-enter-the-liferay-vocabulary-name" />
						
					<aui:fieldset>
						<aui:input label="extract-tags" name="extractTags" type="checkbox"
							value="<%=extractTags%>" />
						<aui:input cssClass="lfr-input-text-container"
							helpMessage="the-domino-field-name-with-tags-is"
							label="domino-field-with-tags" name="dominoFieldNameWithTags"
							value="<%=dominoFieldNameWithTags%>" />
					</aui:fieldset>
					<aui:fieldset>
						<aui:input label="extract-categories" name="extractCategories" type="checkbox"
							value="<%=extractCategories%>" />
						<aui:input cssClass="lfr-input-text-container"
							label="domino-field-with-categories" 
							helpMessage="the-domino-field-name-with-categories-is"
							name="dominoFieldNameWithCategories"
							value="<%=dominoFieldNameWithCategories%>" />
						<aui:input cssClass="lfr-input-text-container"
							label="vocabulary-name" name="vocabularyName"
							value="<%=vocabularyName%>" />
					</aui:fieldset>
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
				data.<portlet:namespace />dominoHostName = '<%= dominoHostName %>'
				data.<portlet:namespace />dominoUserName = '<%= dominoUserName %>'
				data.<portlet:namespace />dominoUserPassword = '<%= dominoUserPassword %>'

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

<aui:script use="liferay-domino-import">
	<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" var="importProcessesURL">
	</liferay-portlet:resourceURL>

	new Liferay.DominoImport(
		{
			form: document.<portlet:namespace />fm1,
			incompleteProcessMessageNode: '#<portlet:namespace />incompleteProcessMessage',
			namespace: '<portlet:namespace />',
			processesNode: '#importProcesses',
			processesResourceURL: '<%= importProcessesURL.toString() %>'
		}
	);
</aui:script>