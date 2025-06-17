<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewFolderSectionDisplayContext viewFolderSectionDisplayContext = (ViewFolderSectionDisplayContext)request.getAttribute(ViewFolderSectionDisplayContext.class.getName());
%>

<div class="cms-section">
	<div>
		<react:component
			module="{Breadcrumb} from site-cms-site-initializer"
			props="<%= viewFolderSectionDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<frontend-data-set:headless-display
		apiURL="<%= viewFolderSectionDisplayContext.getAPIURL() %>"
		bulkActionDropdownItems="<%= viewFolderSectionDisplayContext.getBulkActionDropdownItems() %>"
		creationMenu="<%= viewFolderSectionDisplayContext.getCreationMenu() %>"
		emptyState="<%= viewFolderSectionDisplayContext.getEmptyState() %>"
		fdsActionDropdownItems="<%= viewFolderSectionDisplayContext.getFDSActionDropdownItems() %>"
		formName="fm"
		id="<%= CMSSiteInitializerFDSNames.VIEW_FOLDER %>"
		itemsPerPage="<%= 20 %>"
		propsTransformer="{FolderFDSPropsTransformer} from site-cms-site-initializer"
		selectedItemsKey="id"
		selectionType="multiple"
		style="fluid"
	/>
</div>