<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewBulkActionTaskReportSuccessfulItemsDisplayContext successfulItemsBulkActionTaskReportDisplayContext = (ViewBulkActionTaskReportSuccessfulItemsDisplayContext)request.getAttribute(ViewBulkActionTaskReportSuccessfulItemsDisplayContext.class.getName());
%>

<div class="cms-section custom-empty-state">
	<div>
		<frontend-data-set:headless-display
			apiURL="<%= successfulItemsBulkActionTaskReportDisplayContext.getAPIURL() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.BULK_ACTION_TASK_REPORT_SUCCESSFUL_ITEMS_SECTION %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{BulkActionTaskReportItemsFDSPropsTransformer} from site-cms-site-initializer"
			selectedItemsKey="embedded.id"
			showSelectAll="<%= false %>"
			style="fluid"
		/>
	</div>
</div>