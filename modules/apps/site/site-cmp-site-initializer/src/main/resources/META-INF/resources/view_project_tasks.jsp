<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewProjectTasksSectionDisplayContext viewProjectTasksSectionDisplayContext = (ViewProjectTasksSectionDisplayContext)request.getAttribute(ViewProjectTasksSectionDisplayContext.class.getName());
%>

<div>
	<react:component
		module="{TasksQuickFilters} from site-cmp-site-initializer"
		props="<%= viewProjectTasksSectionDisplayContext.getTasksQuickFiltersProperties() %>"
	/>
</div>

<div class="cms-section custom-empty-state">
	<frontend-data-set:headless-display
		additionalProps="<%= viewProjectTasksSectionDisplayContext.getAdditionalProps() %>"
		apiURL="<%= viewProjectTasksSectionDisplayContext.getAPIURL() %>"
		bulkActionDropdownItems="<%= viewProjectTasksSectionDisplayContext.getBulkActionDropdownItems() %>"
		creationMenu="<%= viewProjectTasksSectionDisplayContext.getCreationMenu() %>"
		emptyState="<%= viewProjectTasksSectionDisplayContext.getEmptyState() %>"
		fdsActionDropdownItems="<%= viewProjectTasksSectionDisplayContext.getFDSActionDropdownItems() %>"
		fdsFilters="<%= viewProjectTasksSectionDisplayContext.getFDSFilters() %>"
		formName="fm"
		id="<%= CMPSiteInitializerFDSNames.CMP_PROJECT_TASKS %>"
		itemsPerPage="<%= 20 %>"
		propsTransformer="{ProjectTasksFDSPropsTransformer} from site-cmp-site-initializer"
		selectedItemsKey="embedded.id"
		selectionType="<%= viewProjectTasksSectionDisplayContext.getSelectionType() %>"
	/>
</div>