<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditSiteTeamAssignmentsUserGroupsDisplayContext editSiteTeamAssignmentsUserGroupsDisplayContext = new EditSiteTeamAssignmentsUserGroupsDisplayContext(request, renderRequest, renderResponse);
%>

<clay:navigation-bar
	navigationItems="<%= editSiteTeamAssignmentsUserGroupsDisplayContext.getNavigationItems() %>"
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new EditSiteTeamAssignmentsUserGroupsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, editSiteTeamAssignmentsUserGroupsDisplayContext) %>"
	propsTransformer="{EditSiteTeamAssignmentsUserGroupsManagementToolbarPropsTransformer} from site-teams-web"
/>

<portlet:actionURL name="deleteTeamUserGroups" var="deleteTeamUserGroupsURL" />

<aui:form action="<%= deleteTeamUserGroupsURL %>" cssClass="container-fluid" method="post" name="fm">
	<aui:input name="tabs1" type="hidden" value="<%= editSiteTeamAssignmentsUserGroupsDisplayContext.getTabs1() %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="teamId" type="hidden" value="<%= String.valueOf(editSiteTeamAssignmentsUserGroupsDisplayContext.getTeamId()) %>" />

	<liferay-ui:search-container
		id="userGroups"
		searchContainer="<%= editSiteTeamAssignmentsUserGroupsDisplayContext.getUserGroupSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.UserGroup"
			escapedModel="<%= true %>"
			keyPropertyName="userGroupId"
			modelVar="userGroup"
		>

			<%
			int usersCount = UserLocalServiceUtil.searchCount(
				company.getCompanyId(), StringPool.BLANK, WorkflowConstants.STATUS_APPROVED,
				LinkedHashMapBuilder.<String, Object>put(
					"usersUserGroups", Long.valueOf(userGroup.getUserGroupId())
				).build());
			%>

			<c:choose>
				<c:when test='<%= Objects.equals(editSiteTeamAssignmentsUserGroupsDisplayContext.getDisplayStyle(), "descriptive") %>'>
					<liferay-ui:search-container-column-icon
						icon="users"
						toggleRowChecker="<%= true %>"
					/>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<div class="h5"><%= userGroup.getName() %></div>

						<div class="h6 text-default">
							<span><%= userGroup.getDescription() %></span>
						</div>

						<div class="h6 text-default">
							<span><liferay-ui:message arguments="<%= usersCount %>" key="x-users" /></span>
						</div>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-jsp
						path="/edit_team_assignments_user_groups_action.jsp"
					/>
				</c:when>
				<c:otherwise>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="name"
						orderable="<%= true %>"
						property="name"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="description"
						orderable="<%= true %>"
						property="description"
					/>

					<liferay-ui:search-container-column-text
						name="users"
						value="<%= String.valueOf(usersCount) %>"
					/>

					<liferay-ui:search-container-column-jsp
						path="/edit_team_assignments_user_groups_action.jsp"
					/>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= editSiteTeamAssignmentsUserGroupsDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<portlet:actionURL name="addTeamUserGroups" var="addTeamUserGroupsURL" />

<aui:form action="<%= addTeamUserGroupsURL %>" cssClass="hide" name="addTeamUserGroupsFm">
	<aui:input name="tabs1" type="hidden" value="<%= editSiteTeamAssignmentsUserGroupsDisplayContext.getTabs1() %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="teamId" type="hidden" value="<%= String.valueOf(editSiteTeamAssignmentsUserGroupsDisplayContext.getTeamId()) %>" />
</aui:form>