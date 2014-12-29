<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/init.jsp" %>

<%
String dominoHostName = ParamUtil.getString(request, "dominoHostName");
String dominoUserName = ParamUtil.getString(request, "dominoUserName");
String dominoUserPassword = ParamUtil.getString(request, "dominoUserPassword");

DominoProxyUtil dominoProxy = DominoProxyUtil.getInstance();
dominoProxy.openDominoSession(dominoHostName, dominoUserName, dominoUserPassword);
%>

<div class="alert alert-info">		
	<c:choose>
		<c:when test="<%= dominoProxy.isDominoSessionAvailable() %>">
			<liferay-ui:message key="liferay-has-successfully-connected-to-the-domino-server" />
		</c:when>
		<c:otherwise>
			<liferay-ui:message key="liferay-has-failed-to-connect-to-the-domino-server" />
		</c:otherwise>
	</c:choose>
</div>
<%
dominoProxy.closeDominoSession();
%>