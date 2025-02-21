<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

@generated
--%>

<%@ include file="/init.jsp" %>

<%
java.lang.String containerId = GetterUtil.getString((java.lang.String)request.getAttribute("liferay-data-engine:data-layout-renderer:containerId"));
java.lang.String contentType = GetterUtil.getString((java.lang.String)request.getAttribute("liferay-data-engine:data-layout-renderer:contentType"));
java.lang.Long dataDefinitionId = GetterUtil.getLong(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-renderer:dataDefinitionId")));
java.lang.Long dataLayoutId = GetterUtil.getLong(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-renderer:dataLayoutId")));
java.lang.Long dataRecordId = GetterUtil.getLong(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-renderer:dataRecordId")));
java.util.Map<java.lang.String, java.lang.Object> dataRecordValues = (java.util.Map<java.lang.String, java.lang.Object>)request.getAttribute("liferay-data-engine:data-layout-renderer:dataRecordValues");
java.lang.String defaultLanguageId = GetterUtil.getString((java.lang.String)request.getAttribute("liferay-data-engine:data-layout-renderer:defaultLanguageId"));
boolean disableFieldRepetition = GetterUtil.getBoolean(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-builder:disableFieldRepetition")));
java.lang.String displayType = GetterUtil.getString((java.lang.String)request.getAttribute("liferay-data-engine:data-layout-renderer:displayType"));
java.lang.String languageId = GetterUtil.getString((java.lang.String)request.getAttribute("liferay-data-engine:data-layout-renderer:languageId"));
java.lang.String namespace = GetterUtil.getString((java.lang.String)request.getAttribute("liferay-data-engine:data-layout-renderer:namespace"));
boolean persistDefaultValues = GetterUtil.getBoolean(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-renderer:persistDefaultValues")));
boolean persisted = GetterUtil.getBoolean(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-renderer:persisted")));
boolean readOnly = GetterUtil.getBoolean(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-renderer:readOnly")));
boolean submittable = GetterUtil.getBoolean(String.valueOf(request.getAttribute("liferay-data-engine:data-layout-renderer:submittable")), true);
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("liferay-data-engine:data-layout-renderer:dynamicAttributes");
%>

<%@ include file="/data_layout_renderer/init-ext.jspf" %>