<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

--%>

<%@ include file="/html/taglib/taglib-init.jsp" %>

<%
java.lang.String cssClass = GetterUtil.getString((java.lang.String)request.getAttribute("aui:col:cssClass"));
java.lang.String id = GetterUtil.getString((java.lang.String)request.getAttribute("aui:col:id"));
java.lang.String lg = GetterUtil.getString((java.lang.String)request.getAttribute("aui:col:lg"));
java.lang.String md = GetterUtil.getString((java.lang.String)request.getAttribute("aui:col:md"));
java.lang.String sm = GetterUtil.getString((java.lang.String)request.getAttribute("aui:col:sm"));
int span = GetterUtil.getInteger(String.valueOf(request.getAttribute("aui:col:span")), 12);
int width = GetterUtil.getInteger(String.valueOf(request.getAttribute("aui:col:width")));
java.lang.String xs = GetterUtil.getString((java.lang.String)request.getAttribute("aui:col:xs"));
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("aui:col:dynamicAttributes");
%>

<%@ include file="/html/taglib/aui/col/init-ext.jspf" %>