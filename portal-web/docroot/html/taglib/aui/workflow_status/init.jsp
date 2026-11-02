<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

--%>

<%@ include file="/html/taglib/taglib-init.jsp" %>

<%
java.lang.Object bean = (java.lang.Object)request.getAttribute("aui:workflow-status:bean");
java.lang.String helpMessage = GetterUtil.getString((java.lang.String)request.getAttribute("aui:workflow-status:helpMessage"));
java.lang.String id = GetterUtil.getString((java.lang.String)request.getAttribute("aui:workflow-status:id"));
java.lang.String markupView = GetterUtil.getString((java.lang.String)request.getAttribute("aui:workflow-status:markupView"));
java.lang.Class<?> model = (java.lang.Class<?>)request.getAttribute("aui:workflow-status:model");
boolean showHelpMessage = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:workflow-status:showHelpMessage")), true);
boolean showLabel = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:workflow-status:showLabel")), true);
java.lang.Integer status = GetterUtil.getInteger(String.valueOf(request.getAttribute("aui:workflow-status:status")));
java.lang.String statusMessage = GetterUtil.getString((java.lang.String)request.getAttribute("aui:workflow-status:statusMessage"));
java.lang.String version = GetterUtil.getString((java.lang.String)request.getAttribute("aui:workflow-status:version"));
%>

<%@ include file="/html/taglib/aui/workflow_status/init-ext.jspf" %>