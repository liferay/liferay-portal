<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %>

<%@ page import="java.util.UUID" %>

<liferay-theme:defineObjects />

<%
String field = (String)request.getAttribute("liferay-commerce:dynamic-field:field");
String fieldValue = (String)request.getAttribute("liferay-commerce:dynamic-field:fieldValue");
String label = (String)request.getAttribute("liferay-commerce:dynamic-field:label");
String labelElementType = (String)request.getAttribute("liferay-commerce:dynamic-field:labelElementType");
String namespace = (String)request.getAttribute("liferay-commerce:dynamic-field:namespace");
String uuid = String.valueOf(UUID.randomUUID());
String valueElementType = (String)request.getAttribute("liferay-commerce:dynamic-field:valueElementType");
%>