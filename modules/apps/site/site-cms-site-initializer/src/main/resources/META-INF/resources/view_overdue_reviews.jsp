<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewOverdueReviewsSectionDisplayContext viewOverdueReviewsSectionDisplayContext = (ViewOverdueReviewsSectionDisplayContext)request.getAttribute(ViewOverdueReviewsSectionDisplayContext.class.getName());
%>

<div class="cms-overdue-reviews-view position-relative">
	<div>
		<react:component
			module="{Breadcrumb} from site-cms-site-initializer"
			props="<%= viewOverdueReviewsSectionDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			additionalProps="<%= viewOverdueReviewsSectionDisplayContext.getAdditionalProps() %>"
			apiURL="<%= viewOverdueReviewsSectionDisplayContext.getAPIURL() %>"
			bulkActionDropdownItems="<%= viewOverdueReviewsSectionDisplayContext.getBulkActionDropdownItems() %>"
			emptyState="<%= viewOverdueReviewsSectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewOverdueReviewsSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.OVERDUE_REVIEWS_SECTION %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{OverdueReviewsFDSPropsTransformer} from site-cms-site-initializer"
			selectedItemsKey="embedded.id"
			selectionType="multiple"
			showSelectAll="<%= true %>"
		/>
	</div>
</div>