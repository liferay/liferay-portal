<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
boolean deleteMenu = GetterUtil.getBoolean(request.getAttribute("liferay-staging:process-list:deleteMenu"));
boolean detailsMenu = GetterUtil.getBoolean(request.getAttribute("liferay-staging:process-list:detailsMenu"));
boolean relaunchMenu = GetterUtil.getBoolean(request.getAttribute("liferay-staging:process-list:relaunchMenu"));
ResultRowSplitter resultRowSplitter = (ResultRowSplitter)request.getAttribute("liferay-staging:process-list:resultRowSplitter");
boolean summaryMenu = GetterUtil.getBoolean(request.getAttribute("liferay-staging:process-list:summaryMenu"));
%>