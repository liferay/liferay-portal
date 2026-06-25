<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.portal.kernel.util.HashMapBuilder" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
String addCommentURL = (String)request.getAttribute("liferay-site-dsr-site-initializer:comments:addCommentURL");
String deleteCommentURL = (String)request.getAttribute("liferay-site-dsr-site-initializer:comments:deleteCommentURL");
String editCommentURL = (String)request.getAttribute("liferay-site-dsr-site-initializer:comments:editCommentURL");
Object editorConfig = (Object)request.getAttribute("liferay-site-dsr-site-initializer:comments:editorConfig");
String getCommentsURL = (String)request.getAttribute("liferay-site-dsr-site-initializer:comments:getCommentsURL");
boolean readOnly = (boolean)request.getAttribute("liferay-site-dsr-site-initializer:comments:readOnly");
long roomId = (long)request.getAttribute("liferay-site-dsr-site-initializer:comments:roomId");
%>