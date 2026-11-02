<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

--%>

<%@ include file="/html/taglib/taglib-init.jsp" %>

<%
boolean collapsed = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:fieldset:collapsed")));
boolean collapsible = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:fieldset:collapsible")));
boolean column = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:fieldset:column")));
java.lang.String cssClass = GetterUtil.getString((java.lang.String)request.getAttribute("aui:fieldset:cssClass"));
java.lang.String helpMessage = GetterUtil.getString((java.lang.String)request.getAttribute("aui:fieldset:helpMessage"));
java.lang.String id = GetterUtil.getString((java.lang.String)request.getAttribute("aui:fieldset:id"));
java.lang.String label = GetterUtil.getString((java.lang.String)request.getAttribute("aui:fieldset:label"));
boolean localizeLabel = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:fieldset:localizeLabel")), true);
java.lang.String markupView = GetterUtil.getString((java.lang.String)request.getAttribute("aui:fieldset:markupView"));
java.lang.String panelHeaderLinkCssClass = GetterUtil.getString((java.lang.String)request.getAttribute("aui:fieldset:panelHeaderLinkCssClass"));
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("aui:fieldset:dynamicAttributes");
%>

<%@ include file="/html/taglib/aui/fieldset/init-ext.jspf" %>