<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewAllSectionDisplayContext viewAllSectionDisplayContext = (ViewAllSectionDisplayContext)request.getAttribute(ViewAllSectionDisplayContext.class.getName());
%>

<div>
	<div>
		<react:component
			module="{Breadcrumb} from site-cms-site-initializer"
			props="<%= viewAllSectionDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			additionalProps="<%= viewAllSectionDisplayContext.getAdditionalProps() %>"
			apiURL="<%= viewAllSectionDisplayContext.getAPIURL() %>"
			bulkActionDropdownItems="<%= viewAllSectionDisplayContext.getBulkActionDropdownItems() %>"
			creationMenu="<%= viewAllSectionDisplayContext.getCreationMenu() %>"
			emptyState="<%= viewAllSectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewAllSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.ALL_SECTION %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{AssetsFilesDropFDSPropsTransformer} from site-cms-site-initializer"
			selectedItemsKey="embedded.id"
			selectionType="multiple"
			showSelectAll="<%= true %>"
			style="fluid"
		/>
	</div>
</div>