<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%@ page import="com.liferay.staging.constants.StagingProcessesPortletKeys" %>

<%
BackgroundTask backgroundTask = (BackgroundTask)request.getAttribute("liferay-staging:process-list-menu:backgroundTask");
boolean deleteMenu = GetterUtil.getBoolean(request.getAttribute("liferay-staging:process-list-menu:deleteMenu"));
boolean detailsMenu = GetterUtil.getBoolean(request.getAttribute("liferay-staging:process-list-menu:detailsMenu"));
boolean localPublishing = GetterUtil.getBoolean(request.getAttribute("liferay-staging:process-list-menu:localPublishing"));
boolean relaunchMenu = GetterUtil.getBoolean(request.getAttribute("liferay-staging:process-list-menu:relaunchMenu"));
boolean summaryMenu = GetterUtil.getBoolean(request.getAttribute("liferay-staging:process-list-menu:summaryMenu"));

Map<String, Serializable> contextMap = backgroundTask.getTaskContextMap();

long exportImportConfigurationId = GetterUtil.getLong(String.valueOf(contextMap.get("exportImportConfigurationId")));

if (exportImportConfigurationId == 0) {
	relaunchMenu = false;
	summaryMenu = false;
}

Date completionDate = backgroundTask.getCompletionDate();

String deleteLabel = LanguageUtil.get(request, ((completionDate != null) && completionDate.before(new Date())) ? "clear" : "cancel");
%>