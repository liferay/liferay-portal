<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewExpiredAssetsSectionDisplayContext viewExpiredAssetsSectionDisplayContext = (ViewExpiredAssetsSectionDisplayContext)request.getAttribute(ViewExpiredAssetsSectionDisplayContext.class.getName());
%>

<div class="cms-expired-assets-view position-relative">
	<div>
		<react:component
			module="{Breadcrumb} from site-cms-site-initializer"
			props="<%= viewExpiredAssetsSectionDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			additionalProps="<%= viewExpiredAssetsSectionDisplayContext.getAdditionalProps() %>"
			apiURL="<%= viewExpiredAssetsSectionDisplayContext.getAPIURL() %>"
			bulkActionDropdownItems="<%= viewExpiredAssetsSectionDisplayContext.getBulkActionDropdownItems() %>"
			emptyState="<%= viewExpiredAssetsSectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewExpiredAssetsSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.EXPIRED_ASSETS_SECTION %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{ExpiredAssetsFDSPropsTransformer} from site-cms-site-initializer"
			selectedItemsKey="embedded.id"
			selectionType="multiple"
			showSelectAll="<%= true %>"
		/>
	</div>
</div>