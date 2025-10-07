<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewContentsSectionDisplayContext viewContentsSectionDisplayContext = (ViewContentsSectionDisplayContext)request.getAttribute(ViewContentsSectionDisplayContext.class.getName());
%>

<div>
	<div>
		<react:component
			module="{Breadcrumb} from site-cms-site-initializer"
			props="<%= viewContentsSectionDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			additionalProps="<%= viewContentsSectionDisplayContext.getAdditionalProps() %>"
			apiURL="<%= viewContentsSectionDisplayContext.getAPIURL() %>"
			bulkActionDropdownItems="<%= viewContentsSectionDisplayContext.getBulkActionDropdownItems() %>"
			creationMenu="<%= viewContentsSectionDisplayContext.getCreationMenu() %>"
			emptyState="<%= viewContentsSectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewContentsSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.CONTENTS_SECTION %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{AssetsFDSPropsTransformer} from site-cms-site-initializer"
			selectedItemsKey="embedded.id"
			selectionType="multiple"
			showSelectAll="<%= true %>"
			style="fluid"
		/>
	</div>
</div>