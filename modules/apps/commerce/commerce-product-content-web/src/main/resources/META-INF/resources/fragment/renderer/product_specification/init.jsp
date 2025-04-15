<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %>

<%@ page import="java.util.UUID" %>

<liferay-theme:defineObjects />

<%
String label = (String)request.getAttribute("liferay-commerce:product-specification:label");
String labelElementType = (String)request.getAttribute("liferay-commerce:product-specification:labelElementType");
String namespace = (String)request.getAttribute("liferay-commerce:product-specification:namespace");
boolean showLabel = (boolean)request.getAttribute("liferay-commerce:product-specification:showLabel");
String value = (String)request.getAttribute("liferay-commerce:product-specification:value");
String valueElementType = (String)request.getAttribute("liferay-commerce:product-specification:valueElementType");
boolean visible = (boolean)request.getAttribute("liferay-commerce:product-specification:visible");

String uuid = String.valueOf(UUID.randomUUID());
%>