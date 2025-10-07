<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewSpaceFilesSummarySectionDisplayContext viewSpaceFilesSummarySectionDisplayContext = (ViewSpaceFilesSummarySectionDisplayContext)request.getAttribute(ViewSpaceFilesSummarySectionDisplayContext.class.getName());
%>

<div class="cms-section">
	<div id="<%= CMSSiteInitializerFDSNames.SPACE_FILES_SUMMARY_SECTION %>">
		<react:component
			module="{SpaceSummaryHeader} from site-cms-site-initializer"
			props="<%= viewSpaceFilesSummarySectionDisplayContext.getHeaderProps() %>"
		/>
	</div>

	<div class="cms-fds-fluid cms-section custom-empty-state">
		<frontend-data-set:headless-display
			additionalProps="<%= viewSpaceFilesSummarySectionDisplayContext.getAdditionalProps() %>"
			apiURL="<%= viewSpaceFilesSummarySectionDisplayContext.getAPIURL() %>"
			creationMenu="<%= viewSpaceFilesSummarySectionDisplayContext.getCreationMenu() %>"
			emptyState="<%= viewSpaceFilesSummarySectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewSpaceFilesSummarySectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.SPACE_FILES_SUMMARY_SECTION %>"
			propsTransformer="{AssetsFDSPropsTransformer} from site-cms-site-initializer"
			showManagementBar="<%= false %>"
			showPagination="<%= false %>"
			showSearch="<%= false %>"
			showSelectAll="<%= false %>"
			style="fluid"
		/>
	</div>
</div>