<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.portal.kernel.util.HashMapBuilder" %>

<%@ page import="java.util.Map" %><%@
page import="java.util.UUID" %>

<liferay-theme:defineObjects />

<%
Map<String, Object> additionalProps = (Map<String, Object>)request.getAttribute("liferay-commerce:info-box:additionalProps");
String buttonStyle = (String)request.getAttribute("liferay-commerce:info-box:buttonStyle");
long commerceOrderId = (long)request.getAttribute("liferay-commerce:info-box:commerceOrderId");
String field = (String)request.getAttribute("liferay-commerce:info-box:field");
String fieldValue = (String)request.getAttribute("liferay-commerce:info-box:fieldValue");
String fieldValueType = (String)request.getAttribute("liferay-commerce:info-box:fieldValueType");
boolean hasManageOrderNotesPermission = (boolean)request.getAttribute("liferay-commerce:info-box:hasManageOrderNotesPermission");
boolean hasManageOrderRestrictedNotesPermission = (boolean)request.getAttribute("liferay-commerce:info-box:hasManageOrderRestrictedNotesPermission");
boolean hasUpdatePermission = (boolean)request.getAttribute("liferay-commerce:info-box:hasUpdatePermission");
boolean hasViewPermission = (boolean)request.getAttribute("liferay-commerce:info-box:hasViewPermission");
String label = (String)request.getAttribute("liferay-commerce:info-box:label");
String namespace = (String)request.getAttribute("liferay-commerce:info-box:namespace");
boolean open = (boolean)request.getAttribute("liferay-commerce:info-box:open");
boolean readOnly = (boolean)request.getAttribute("liferay-commerce:info-box:readOnly");
String uuid = String.valueOf(UUID.randomUUID());
%>