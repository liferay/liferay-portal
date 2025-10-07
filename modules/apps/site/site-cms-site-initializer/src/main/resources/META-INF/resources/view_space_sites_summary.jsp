<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewSpaceSitesSummarySectionDisplayContext viewSpaceSitesSummarySectionDisplayContext = (ViewSpaceSitesSummarySectionDisplayContext)request.getAttribute(ViewSpaceSitesSummarySectionDisplayContext.class.getName());
%>

<div class="cms-section">
	<div id="<%= CMSSiteInitializerFDSNames.SPACE_SITES_SUMMARY_SECTION %>">
		<react:component
			module="{SpaceSummaryHeader} from site-cms-site-initializer"
			props="<%= viewSpaceSitesSummarySectionDisplayContext.getHeaderProps() %>"
		/>
	</div>

	<div class="cms-fds-fluid cms-section custom-empty-state" data-testid="space-summary-connected-sites">
		<frontend-data-set:headless-display
			apiURL="<%= viewSpaceSitesSummarySectionDisplayContext.getAPIURL() %>"
			creationMenu="<%= viewSpaceSitesSummarySectionDisplayContext.getCreationMenu() %>"
			emptyState="<%= viewSpaceSitesSummarySectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewSpaceSitesSummarySectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.SPACE_SITES_SUMMARY_SECTION %>"
			propsTransformer="{SitesFDSPropsTransformer} from site-cms-site-initializer"
			showManagementBar="<%= false %>"
			showPagination="<%= false %>"
			showSearch="<%= false %>"
			showSelectAll="<%= false %>"
			style="fluid"
		/>
	</div>
</div>