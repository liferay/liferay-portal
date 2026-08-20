<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewBrokenLinksSectionDisplayContext viewBrokenLinksSectionDisplayContext = (ViewBrokenLinksSectionDisplayContext)request.getAttribute(ViewBrokenLinksSectionDisplayContext.class.getName());
%>

<div class="cms-broken-links-view position-relative">
	<div>
		<react:component
			module="{Breadcrumb} from site-cms-site-initializer"
			props="<%= viewBrokenLinksSectionDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			additionalProps="<%= viewBrokenLinksSectionDisplayContext.getAdditionalProps() %>"
			apiURL="<%= viewBrokenLinksSectionDisplayContext.getAPIURL() %>"
			emptyState="<%= viewBrokenLinksSectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewBrokenLinksSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.BROKEN_LINKS_SECTION %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{BrokenLinksFDSPropsTransformer} from site-cms-site-initializer"
			uniformActionsDisplay="<%= true %>"
		/>
	</div>
</div>