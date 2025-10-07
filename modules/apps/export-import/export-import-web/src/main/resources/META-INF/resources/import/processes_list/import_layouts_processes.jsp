<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/import/init.jsp" %>

<liferay-staging:process-list
	deleteMenu="<%= true %>"
	detailsMenu='<%= FeatureFlagManagerUtil.isEnabled(company.getCompanyId(), "LPD-35914") %>'
	emptyResultsMessage="no-publish-processes-were-found"
	localTaskExecutorClassName="<%= BackgroundTaskExecutorNames.LAYOUT_IMPORT_BACKGROUND_TASK_EXECUTOR %>"
	mvcRenderCommandName="/export_import/view_import_layouts"
	relaunchMenu="<%= false %>"
	resultRowSplitter="<%= new ExportImportResultRowSplitter() %>"
	summaryMenu="<%= false %>"
/>