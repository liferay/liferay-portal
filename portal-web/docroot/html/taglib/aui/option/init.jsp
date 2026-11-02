<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

--%>

<%@ include file="/html/taglib/taglib-init.jsp" %>

<%
java.lang.String cssClass = GetterUtil.getString((java.lang.String)request.getAttribute("aui:option:cssClass"));
java.util.Map<java.lang.String, java.lang.Object> data = (java.util.Map<java.lang.String, java.lang.Object>)request.getAttribute("aui:option:data");
boolean disabled = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:option:disabled")));
java.lang.Object label = (java.lang.Object)request.getAttribute("aui:option:label");
boolean localizeLabel = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:option:localizeLabel")), true);
boolean selected = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:option:selected")));
java.lang.String style = GetterUtil.getString((java.lang.String)request.getAttribute("aui:option:style"));
java.lang.Object value = (java.lang.Object)request.getAttribute("aui:option:value");
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("aui:option:dynamicAttributes");
%>

<%@ include file="/html/taglib/aui/option/init-ext.jspf" %>