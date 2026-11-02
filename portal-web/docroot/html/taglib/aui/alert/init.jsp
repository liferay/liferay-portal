<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

--%>

<%@ include file="/html/taglib/taglib-init.jsp" %>

<%
boolean animated = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:alert:animated")));
boolean closeable = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:alert:closeable")), true);
java.lang.String cssClass = GetterUtil.getString((java.lang.String)request.getAttribute("aui:alert:cssClass"));
boolean destroyOnHide = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:alert:destroyOnHide")));
java.lang.Number duration = GetterUtil.getNumber(String.valueOf(request.getAttribute("aui:alert:duration")), 0.15);
java.lang.String id = GetterUtil.getString((java.lang.String)request.getAttribute("aui:alert:id"));
java.lang.String type = GetterUtil.getString((java.lang.String)request.getAttribute("aui:alert:type"), "info");
%>

<%@ include file="/html/taglib/aui/alert/init-ext.jspf" %>