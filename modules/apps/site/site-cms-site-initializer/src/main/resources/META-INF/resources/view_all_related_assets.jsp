<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewAllRelatedAssetsSectionDisplayContext viewAllRelatedAssetsSectionDisplayContext = (ViewAllRelatedAssetsSectionDisplayContext)request.getAttribute(ViewAllRelatedAssetsSectionDisplayContext.class.getName());
%>

<div class="cms-all-related-assets cms-section custom-empty-state"></div>
	<frontend-data-set:headless-display
		additionalProps="<%= viewAllRelatedAssetsSectionDisplayContext.getAdditionalProps() %>"
		apiURL="<%= viewAllRelatedAssetsSectionDisplayContext.getAPIURL() %>"
		emptyState="<%= viewAllRelatedAssetsSectionDisplayContext.getEmptyState() %>"
		fdsActionDropdownItems="<%= viewAllRelatedAssetsSectionDisplayContext.getFDSActionDropdownItems() %>"
		formName="fm"
		id="<%= CMSSiteInitializerFDSNames.ALL_RELATED_ASSETS_SECTION %>"
		propsTransformer="{AssetsFDSPropsTransformer} from site-cms-site-initializer"
		selectedItemsKey="embedded.id"
		selectionType="multiple"
		showSelectAll="<%= true %>"
	/>
</div>