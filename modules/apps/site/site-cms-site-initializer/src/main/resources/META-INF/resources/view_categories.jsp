<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewCategoriesDisplayContext viewCategoriesDisplayContext = (ViewCategoriesDisplayContext)request.getAttribute(ViewCategoriesDisplayContext.class.getName());
%>

<div class="cms-section">
	<div class="categorization-section">
		<div>
			<react:component
				module="{Breadcrumb} from site-cms-site-initializer"
				props="<%= viewCategoriesDisplayContext.getBreadcrumbProps() %>"
			/>
		</div>

		<frontend-data-set:headless-display
			apiURL="<%= viewCategoriesDisplayContext.getAPIURL() %>"
			creationMenu="<%= viewCategoriesDisplayContext.getCreationMenu() %>"
			emptyState="<%= viewCategoriesDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewCategoriesDisplayContext.getFDSActionDropdownItems() %>"
			id="<%= CMSSiteInitializerFDSNames.CATEGORIES %>"
			propsTransformer="{CategoryFDSPropsTransformer} from site-cms-site-initializer"
			selectedItemsKey="id"
			selectionType="multiple"
		/>
	</div>
</div>