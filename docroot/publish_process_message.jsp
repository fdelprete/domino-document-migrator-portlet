<%@ include file="/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

BackgroundTask backgroundTask = (BackgroundTask)row.getObject();
%>

<strong class="label background-task-status-<%= BackgroundTaskConstants.getStatusLabel(backgroundTask.getStatus()) %> <%= BackgroundTaskConstants.getStatusCssClass(backgroundTask.getStatus()) %>">
	<liferay-ui:message key="<%= backgroundTask.getStatusLabel() %>" />
</strong>

<c:if test="<%= backgroundTask.isInProgress() %>">

	<%
	BackgroundTaskStatus backgroundTaskStatus = BackgroundTaskStatusRegistryUtil.getBackgroundTaskStatus(backgroundTask.getBackgroundTaskId());
	%>

	<c:if test="<%= backgroundTaskStatus != null %>">

		<%
		double percentage = 100;

		int documentsImported = GetterUtil.getInteger(backgroundTaskStatus.getAttribute("documentsImported"));
		int documentsWithProblem = GetterUtil.getInteger(backgroundTaskStatus.getAttribute("documentsWithProblem"));
		int documentParsed = documentsImported + documentsWithProblem;
		int totalDocuments = GetterUtil.getInteger(backgroundTaskStatus.getAttribute("totalDocuments"));

		if (totalDocuments > 0) {
			percentage = Math.round((double)documentParsed / totalDocuments * 100);
		}
		%>

		<div class="progress progress-striped active">
			<div class="bar" style="width: <%= percentage %>%;">
				<c:if test="<%= totalDocuments > 0 %>">
					<%= documentParsed %> / <%= totalDocuments %>
				</c:if>
			</div>
		</div>

		<div class="progress-current-item">
			<strong><liferay-ui:message key="importing" /><%= StringPool.TRIPLE_PERIOD %></strong> 
		</div>

	</c:if>
</c:if>

