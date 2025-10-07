<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewBulkActionTaskReportDisplayContext viewBulkActionTaskReportDisplayContext = (ViewBulkActionTaskReportDisplayContext)request.getAttribute(ViewBulkActionTaskReportDisplayContext.class.getName());
%>

<div class="cms-section custom-empty-state">
	<div>
		<frontend-data-set:headless-display
			apiURL="<%= viewBulkActionTaskReportDisplayContext.getAPIURL() %>"
			fdsActionDropdownItems="<%= viewBulkActionTaskReportDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.BULK_ACTION_TASK_REPORT_SECTION %>"
			itemsPerPage="<%= 20 %>"
			selectedItemsKey="embedded.id"
			showSelectAll="<%= false %>"
			style="fluid"
		/>
	</div>
</div>