
<%@ include file="/init.jsp" %>

<%
int incompleteBackgroundTaskCount = ParamUtil.getInteger(request, "incompleteBackgroundTaskCount");
%>

<div class="alert alert-info">
	<c:choose>
		<c:when test="<%= incompleteBackgroundTaskCount == 1 %>">
			<liferay-ui:message key="there-is-currently-1-process-in-progress" />
		</c:when>
		<c:when test="<%= incompleteBackgroundTaskCount > 1 %>">
			<liferay-ui:message arguments="<%= incompleteBackgroundTaskCount - 1 %>" key="there-is-currently-1-process-in-progress-and-x-pending" />
		</c:when>
		<c:otherwise>
			<liferay-ui:message key="there-are-no-processes-in-progress-anymore" />
		</c:otherwise>
	</c:choose>
</div>