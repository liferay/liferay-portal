<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

@generated
--%>

<%@ include file="/init.jsp" %>

<%
java.util.List<java.util.Map<java.lang.String, java.lang.Object>> additionalPanels = (java.util.List<java.util.Map<java.lang.String, java.lang.Object>>)request.getAttribute("liferay-data-engine:data-layout-builder:additionalPanels");
java.lang.String componentId = GetterUtil.getString((java.lang.String)request.getAttribute("liferay-data-engine:data-layout-builder:componentId"));
java.lang.String contentType = GetterUtil.getString((java.lang.String)request.getAttribute("liferay-data-engine:data-layout-builder:contentType"));
java.lang.Long dataDefinitionId = GetterUtil.getLong(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-builder:dataDefinitionId")));
java.lang.Long dataLayoutId = GetterUtil.getLong(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-builder:dataLayoutId")));
boolean displayFieldName = GetterUtil.getBoolean(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-builder:displayFieldName")));
java.lang.String fieldSetContentType = GetterUtil.getString((java.lang.String)request.getAttribute("liferay-data-engine:data-layout-builder:fieldSetContentType"));
java.lang.Long groupId = GetterUtil.getLong(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-builder:groupId")));
boolean localizable = GetterUtil.getBoolean(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-builder:localizable")));
java.lang.String module = GetterUtil.getString((java.lang.String)request.getAttribute("liferay-data-engine:data-layout-builder:module"));
jakarta.servlet.ServletContext moduleServletContext = (jakarta.servlet.ServletContext)request.getAttribute("liferay-data-engine:data-layout-builder:moduleServletContext");
java.lang.String namespace = GetterUtil.getString((java.lang.String)request.getAttribute("liferay-data-engine:data-layout-builder:namespace"));
java.lang.Object scopes = (java.lang.Object)request.getAttribute("liferay-data-engine:data-layout-builder:scopes");
boolean searchableFieldsDisabled = GetterUtil.getBoolean(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-builder:searchableFieldsDisabled")));
java.lang.String submitButtonId = GetterUtil.getString((java.lang.String)request.getAttribute("liferay-data-engine:data-layout-builder:submitButtonId"));
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("liferay-data-engine:data-layout-builder:dynamicAttributes");
%>

<%@ include file="/data_layout_builder/init-ext.jspf" %>