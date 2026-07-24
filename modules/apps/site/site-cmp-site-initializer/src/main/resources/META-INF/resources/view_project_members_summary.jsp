<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewProjectMembersSummarySectionDisplayContext viewProjectMembersSummarySectionDisplayContext = (ViewProjectMembersSummarySectionDisplayContext)request.getAttribute(ViewProjectMembersSummarySectionDisplayContext.class.getName());
%>

<div class="cms-section cms-tabs-fluid">
	<div id="<%= CMPSiteInitializerFDSNames.CMP_PROJECT_MEMBERS_SUMMARY %>">
		<react:component
			module="{ProjectSummaryHeader} from site-cmp-site-initializer"
			props="<%= viewProjectMembersSummarySectionDisplayContext.getHeaderProps() %>"
		/>
	</div>

	<clay:tabs
		tabsItems="<%= viewProjectMembersSummarySectionDisplayContext.getTabsItems() %>"
	>
		<clay:tabs-panel>
			<div class="cms-fds-fluid cms-space-summary custom-empty-state">
				<frontend-data-set:headless-display
					apiURL='<%= viewProjectMembersSummarySectionDisplayContext.getAPIURL("user-accounts") %>'
					creationMenu="<%= viewProjectMembersSummarySectionDisplayContext.getCreationMenu() %>"
					emptyState="<%= viewProjectMembersSummarySectionDisplayContext.getEmptyState() %>"
					formName="fm"
					id="<%= CMPSiteInitializerFDSNames.CMP_PROJECT_MEMBERS_USERS_SUMMARY %>"
					propsTransformer="{ProjectMembersFDSPropsTransformer} from site-cmp-site-initializer"
					showManagementBar="<%= false %>"
					showPagination="<%= false %>"
					showSearch="<%= false %>"
					showSelectAll="<%= false %>"
					style="fluid"
				/>
			</div>
		</clay:tabs-panel>

		<clay:tabs-panel>
			<div class="cms-fds-fluid cms-space-summary custom-empty-state">
				<frontend-data-set:headless-display
					apiURL='<%= viewProjectMembersSummarySectionDisplayContext.getAPIURL("user-groups") %>'
					creationMenu="<%= viewProjectMembersSummarySectionDisplayContext.getCreationMenu() %>"
					emptyState="<%= viewProjectMembersSummarySectionDisplayContext.getEmptyState() %>"
					formName="fm"
					id="<%= CMPSiteInitializerFDSNames.CMP_PROJECT_MEMBERS_USER_GROUPS_SUMMARY %>"
					propsTransformer="{ProjectMembersFDSPropsTransformer} from site-cmp-site-initializer"
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