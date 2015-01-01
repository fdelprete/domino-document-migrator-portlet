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

<div class="alert alert-info">		

<%
String dominoHostName = ParamUtil.getString(request, "dominoHostName");
String dominoUserName = ParamUtil.getString(request, "dominoUserName");
String dominoUserPassword = ParamUtil.getString(request, "dominoUserPassword");

if (Validator.isNull(ParamUtil.getString(request, "dominoDatabaseName")) ||
		Validator.isNull(ParamUtil.getString(request, "dominoViewName")) ||
		Validator.isNull(ParamUtil.getString(request, "dominoFieldName"))) {
%>
	<liferay-ui:message key="please-define-each-of-the-domino-properties-database-name,-view-name-and-field-name" />

<%
	return;
}
DominoProxyUtil dominoProxy = DominoProxyUtil.getInstance();
dominoProxy.openDominoSession(dominoHostName, dominoUserName, dominoUserPassword);


if (!dominoProxy.isDominoSessionAvailable()) {
%>
	<liferay-ui:message key="liferay-has-successfully-connected-to-the-domino-server" />
<%
	return;
}

String dominoDatabaseName = ParamUtil.getString(request, "dominoDatabaseName");
String server = dominoProxy.dominoSession.getServerName();
Database db = dominoProxy.dominoSession.getDatabase(server, dominoDatabaseName);
if (!db.isOpen()) {
%>
	<liferay-ui:message key="the-database-does-not-exist-on-server" />
<%
	return;
}
String dominoViewName = ParamUtil.getString(request, "dominoViewName");
View view = db.getView(dominoViewName);
if (view == null ) {
%>
	<liferay-ui:message key="the-specified-view-does-not-exist-on-database" />
<% 	
	return;
}
Document doc = view.getFirstDocument();
if (doc == null ) {
%>
	<liferay-ui:message key="no-doc-exists-on-specified-view" />
<% 	
	return;
}

String dominoFieldName = ParamUtil.getString(request, "dominoFieldName");
if(doc.hasItem(dominoFieldName)) {
	Item theItem =  doc.getFirstItem(dominoFieldName);
	if (theItem.getType() == Item.RICHTEXT) {
		RichTextItem  rtitem = (RichTextItem) doc.getFirstItem(dominoFieldName);
		Vector v = rtitem.getEmbeddedObjects();
		int size = v.size();
		Enumeration e = v.elements();
		String sourceFile = "";
		while (e.hasMoreElements()) {
	        EmbeddedObject eo = (EmbeddedObject)e.nextElement();
	        if (eo.getType() == EmbeddedObject.EMBED_ATTACHMENT) {
	          sourceFile = eo.getSource();
	          }
	      }
	} else {
%>
	<liferay-ui:message key="the-specified-field-is-not-a-rich-text-item" />
<%
		return;
	}
} else {
%>
	<liferay-ui:message key="the-richtext-field-does-not-exist-on-first-doc-in-the-view" />
<%
	return;
}
doc.recycle();
view.recycle();
%>
	<liferay-ui:message key="liferay-has-successfully-opened-the-domino-database-on-server" />
</div>
<%
if (dominoProxy.isDominoSessionAvailable())
	dominoProxy.closeDominoSession();
%>