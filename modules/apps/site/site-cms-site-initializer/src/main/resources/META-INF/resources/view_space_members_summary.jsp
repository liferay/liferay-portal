<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewSpaceMembersSummarySectionDisplayContext viewSpaceMembersSummarySectionDisplayContext = (ViewSpaceMembersSummarySectionDisplayContext)request.getAttribute(ViewSpaceMembersSummarySectionDisplayContext.class.getName());
%>

<div class="cms-section cms-tabs-fluid">
	<div id="<%= CMSSiteInitializerFDSNames.SPACE_MEMBERS_SUMMARY_SECTION %>">
		<react:component
			module="{SpaceSummaryHeader} from site-cms-site-initializer"
			props="<%= viewSpaceMembersSummarySectionDisplayContext.getHeaderProps() %>"
		/>
	</div>

	<clay:tabs
		tabsItems="<%= viewSpaceMembersSummarySectionDisplayContext.getTabsItems() %>"
	>
		<clay:tabs-panel>
			<div class="cms-fds-fluid cms-section custom-empty-state">
				<frontend-data-set:headless-display
					apiURL='<%= viewSpaceMembersSummarySectionDisplayContext.getAPIURL("user-accounts") %>'
					creationMenu="<%= viewSpaceMembersSummarySectionDisplayContext.getCreationMenu() %>"
					emptyState="<%= viewSpaceMembersSummarySectionDisplayContext.getEmptyState() %>"
					formName="fm"
					id="<%= CMSSiteInitializerFDSNames.SPACE_MEMBERS_USERS_SUMMARY_SECTION %>"
					propsTransformer="{MembersFDSPropsTransformer} from site-cms-site-initializer"
					showManagementBar="<%= false %>"
					showPagination="<%= false %>"
					showSearch="<%= false %>"
					showSelectAll="<%= false %>"
					style="fluid"
				/>
			</div>
		</clay:tabs-panel>

		<clay:tabs-panel>
			<div class="cms-fds-fluid cms-section custom-empty-state">
				<frontend-data-set:headless-display
					apiURL='<%= viewSpaceMembersSummarySectionDisplayContext.getAPIURL("user-groups") %>'
					creationMenu="<%= viewSpaceMembersSummarySectionDisplayContext.getCreationMenu() %>"
					emptyState="<%= viewSpaceMembersSummarySectionDisplayContext.getEmptyState() %>"
					formName="fm"
					id="<%= CMSSiteInitializerFDSNames.SPACE_MEMBERS_USER_GROUPS_SUMMARY_SECTION %>"
					propsTransformer="{MembersFDSPropsTransformer} from site-cms-site-initializer"
					showManagementBar="<%= false %>"
					showPagination="<%= false %>"
					showSearch="<%= false %>"
					showSelectAll="<%= false %>"
					style="fluid"
				/>
			</div>
		</clay:tabs-panel>
	</clay:tabs>
</div>