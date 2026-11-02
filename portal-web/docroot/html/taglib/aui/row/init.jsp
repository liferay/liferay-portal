<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

--%>

<%@ include file="/html/taglib/taglib-init.jsp" %>

<%
java.lang.String cssClass = GetterUtil.getString((java.lang.String)request.getAttribute("aui:row:cssClass"));
java.lang.String id = GetterUtil.getString((java.lang.String)request.getAttribute("aui:row:id"));
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("aui:row:dynamicAttributes");
%>

<%@ include file="/html/taglib/aui/row/init-ext.jspf" %>