<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

--%>

<%@ include file="/html/taglib/taglib-init.jsp" %>

<%
java.lang.String cssClass = GetterUtil.getString((java.lang.String)request.getAttribute("aui:field-wrapper:cssClass"));
java.util.Map data = (java.util.Map)request.getAttribute("aui:field-wrapper:data");
boolean disabled = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:field-wrapper:disabled")));
boolean first = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:field-wrapper:first")));
java.lang.String helpMessage = GetterUtil.getString((java.lang.String)request.getAttribute("aui:field-wrapper:helpMessage"));
boolean inlineField = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:field-wrapper:inlineField")));
java.lang.String inlineLabel = GetterUtil.getString((java.lang.String)request.getAttribute("aui:field-wrapper:inlineLabel"));
java.lang.String label = GetterUtil.getString((java.lang.String)request.getAttribute("aui:field-wrapper:label"));
boolean last = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:field-wrapper:last")));
boolean localizeLabel = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:field-wrapper:localizeLabel")), true);
java.lang.String name = GetterUtil.getString((java.lang.String)request.getAttribute("aui:field-wrapper:name"));
boolean required = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:field-wrapper:required")));
%>

<%@ include file="/html/taglib/aui/field_wrapper/init-ext.jspf" %>