<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewSpaceContentsSummarySectionDisplayContext viewSpaceContentsSummarySectionDisplayContext = (ViewSpaceContentsSummarySectionDisplayContext)request.getAttribute(ViewSpaceContentsSummarySectionDisplayContext.class.getName());
%>

<div class="cms-section">
	<div id="<%= CMSSiteInitializerFDSNames.SPACE_CONTENTS_SUMMARY_SECTION %>">
		<react:component
			module="{SpaceSummaryHeader} from site-cms-site-initializer"
			props="<%= viewSpaceContentsSummarySectionDisplayContext.getHeaderProps() %>"
		/>
	</div>

	<div class="cms-fds-fluid cms-section custom-empty-state">
		<frontend-data-set:headless-display
			additionalProps="<%= viewSpaceContentsSummarySectionDisplayContext.getAdditionalProps() %>"
			apiURL="<%= viewSpaceContentsSummarySectionDisplayContext.getAPIURL() %>"
			creationMenu="<%= viewSpaceContentsSummarySectionDisplayContext.getCreationMenu() %>"
			emptyState="<%= viewSpaceContentsSummarySectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewSpaceContentsSummarySectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.SPACE_CONTENTS_SUMMARY_SECTION %>"
			propsTransformer="{AssetsFDSPropsTransformer} from site-cms-site-initializer"
			showManagementBar="<%= false %>"
			showPagination="<%= false %>"
			showSearch="<%= false %>"
			showSelectAll="<%= false %>"
			style="fluid"
		/>
	</div>
</div>