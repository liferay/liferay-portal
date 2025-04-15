<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %>

<liferay-theme:defineObjects />

<%
long commerceOrderId = (long)request.getAttribute("liferay-commerce:order-summary:commerceOrderId");
String field = (String)request.getAttribute("liferay-commerce:order-summary:field");
String fieldLabel = (String)request.getAttribute("liferay-commerce:order-summary:fieldLabel");
String fieldValue = (String)request.getAttribute("liferay-commerce:order-summary:fieldValue");
String label = (String)request.getAttribute("liferay-commerce:order-summary:label");
boolean open = (boolean)request.getAttribute("liferay-commerce:order-summary:open");
%>