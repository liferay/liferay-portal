<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %>

<%@ page import="java.util.Map" %>

<liferay-theme:defineObjects />

<%
Map<String, Object> actionContext = (Map<String, Object>)request.getAttribute("liferay-commerce:info-box:actionContext");
String actionLabel = (String)request.getAttribute("liferay-commerce:info-box:actionLabel");
String actionTargetId = (String)request.getAttribute("liferay-commerce:info-box:actionTargetId");
String actionUrl = (String)request.getAttribute("liferay-commerce:info-box:actionUrl");
String elementClasses = (String)request.getAttribute("liferay-commerce:info-box:elementClasses");
String title = (String)request.getAttribute("liferay-commerce:info-box:title");
%>