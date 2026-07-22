<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewPendingWorkflowsSectionDisplayContext viewPendingWorkflowsSectionDisplayContext = (ViewPendingWorkflowsSectionDisplayContext)request.getAttribute(ViewPendingWorkflowsSectionDisplayContext.class.getName());
%>

<div class="cms-pending-workflows-view position-relative">
	<div>
		<react:component
			module="{Breadcrumb} from site-cms-site-initializer"
			props="<%= viewPendingWorkflowsSectionDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			additionalProps="<%= viewPendingWorkflowsSectionDisplayContext.getAdditionalProps() %>"
			apiURL="<%= viewPendingWorkflowsSectionDisplayContext.getAPIURL() %>"
			emptyState="<%= viewPendingWorkflowsSectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewPendingWorkflowsSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.PENDING_WORKFLOWS_SECTION %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{PendingWorkflowsFDSPropsTransformer} from site-cms-site-initializer"
		/>
	</div>
</div>