<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewAllTasksSectionDisplayContext viewAllTasksSectionDisplayContext = (ViewAllTasksSectionDisplayContext)request.getAttribute(ViewAllTasksSectionDisplayContext.class.getName());
%>

<div class="cms-section custom-empty-state">
	<frontend-data-set:headless-display
		additionalProps="<%= viewAllTasksSectionDisplayContext.getAdditionalProps() %>"
		apiURL="<%= viewAllTasksSectionDisplayContext.getAPIURL() %>"
		bulkActionDropdownItems="<%= viewAllTasksSectionDisplayContext.getBulkActionDropdownItems() %>"
		creationMenu="<%= viewAllTasksSectionDisplayContext.getCreationMenu() %>"
		emptyState="<%= viewAllTasksSectionDisplayContext.getEmptyState() %>"
		fdsActionDropdownItems="<%= viewAllTasksSectionDisplayContext.getFDSActionDropdownItems() %>"
		fdsFilters="<%= viewAllTasksSectionDisplayContext.getFDSFilters() %>"
		formName="fm"
		id="<%= CMPSiteInitializerFDSNames.CMP_ALL_TASKS %>"
		itemsPerPage="<%= 20 %>"
		propsTransformer="{AllTasksFDSPropsTransformer} from site-cmp-site-initializer"
		selectedItemsKey="embedded.id"
		selectionType="<%= viewAllTasksSectionDisplayContext.getSelectionType() %>"
	/>
</div>