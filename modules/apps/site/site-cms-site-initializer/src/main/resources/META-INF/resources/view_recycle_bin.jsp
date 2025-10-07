<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewRecycleBinSectionDisplayContext viewRecycleBinSectionDisplayContext = (ViewRecycleBinSectionDisplayContext)request.getAttribute(ViewRecycleBinSectionDisplayContext.class.getName());
%>

<div class="cms-section custom-empty-state">
	<div class="recycle-bin-section">
		<div>
			<react:component
				module="{RecycleBinToolbar} from site-cms-site-initializer"
			/>
		</div>

		<div>
			<react:component
				module="{Breadcrumb} from site-cms-site-initializer"
				props="<%= viewRecycleBinSectionDisplayContext.getBreadcrumbProps() %>"
			/>
		</div>

		<frontend-data-set:headless-display
			apiURL="<%= viewRecycleBinSectionDisplayContext.getAPIURL() %>"
			bulkActionDropdownItems="<%= viewRecycleBinSectionDisplayContext.getBulkActionDropdownItems() %>"
			emptyState="<%= viewRecycleBinSectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewRecycleBinSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.RECYCLE_BIN_SECTION %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{RecycleBinFDSPropsTransformer} from site-cms-site-initializer"
			selectedItemsKey="embedded.id"
			selectionType="multiple"
			showSelectAll="<%= true %>"
			style="fluid"
		/>
	</div>
</div>