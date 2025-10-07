<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewHomeRecentAssetsSectionDisplayContext viewHomeRecentAssetsSectionDisplayContext = (ViewHomeRecentAssetsSectionDisplayContext)request.getAttribute(ViewHomeRecentAssetsSectionDisplayContext.class.getName());
%>

<div class="cms-section">
	<div class="container-fluid">
		<div class="align-items-center d-flex justify-content-between">
			<span class="font-weight-semi-bold text-4">Recent Assets</span>

			<a class="btn btn-link btn-sm font-weight-semi-bold" href="<%= viewHomeRecentAssetsSectionDisplayContext.getAssetsAllURL() %>">View All</a>
		</div>

		<div class="cms-fds-fluid cms-section custom-empty-state">
			<frontend-data-set:headless-display
				additionalProps="<%= viewHomeRecentAssetsSectionDisplayContext.getAdditionalProps() %>"
				apiURL="<%= viewHomeRecentAssetsSectionDisplayContext.getAPIURL() %>"
				emptyState="<%= viewHomeRecentAssetsSectionDisplayContext.getEmptyState() %>"
				fdsActionDropdownItems="<%= viewHomeRecentAssetsSectionDisplayContext.getFDSActionDropdownItems() %>"
				formName="fm"
				id="<%= CMSSiteInitializerFDSNames.HOME_RECENT_ASSETS_SECTION %>"
				itemsPerPage="<%= 20 %>"
				propsTransformer="{HomeRecentAssetsFDSPropsTransformer} from site-cms-site-initializer"
				showManagementBar="<%= false %>"
				showSearch="<%= false %>"
				style="fluid"
			/>
		</div>
	</div>
</div>