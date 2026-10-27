<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewTasksDisplayContext viewTasksDisplayContext = (ViewTasksDisplayContext)request.getAttribute(ViewTasksDisplayContext.class.getName());
%>

<div>
	<div>
		<react:component
			module="{Breadcrumb} from site-cms-site-initializer"
			props="<%= viewTasksDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			apiURL="<%= viewTasksDisplayContext.getAPIURL() %>"
			emptyState="<%= viewTasksDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewTasksDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.TASKS_SECTION %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{TasksFDSPropsTransformer} from site-cms-site-initializer"
		/>
	</div>
</div>