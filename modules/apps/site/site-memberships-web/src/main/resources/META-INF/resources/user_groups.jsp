<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
UserGroupsDisplayContext userGroupsDisplayContext = new UserGroupsDisplayContext(request, renderRequest, renderResponse);

UserGroupsManagementToolbarDisplayContext userGroupsManagementToolbarDisplayContext = new UserGroupsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, userGroupsDisplayContext);

Role role = userGroupsDisplayContext.getRole();
%>

<clay:navigation-bar
	navigationItems="<%= siteMembershipsDisplayContext.getViewNavigationItems() %>"
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= userGroupsManagementToolbarDisplayContext %>"
	propsTransformer="{UserGroupsManagementToolbarPropsTransformer} from site-memberships-web"
/>

<portlet:actionURL name="deleteGroupUserGroups" var="deleteGroupUserGroupsURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= deleteGroupUserGroupsURL %>" cssClass="container-fluid portlet-site-memberships-user-groups" name="fm">
	<aui:input name="tabs1" type="hidden" value="user-groups" />
	<aui:input name="navigation" type="hidden" value="<%= userGroupsDisplayContext.getNavigation() %>" />
	<aui:input name="roleId" type="hidden" value="<%= (role != null) ? role.getRoleId() : 0 %>" />

	<liferay-site-navigation:breadcrumb
		breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, true, false, false, true, true) %>"
	/>

	<liferay-ui:search-container
		id="userGroups"
		searchContainer="<%= userGroupsDisplayContext.getUserGroupSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.UserGroup"
			escapedModel="<%= true %>"
			keyPropertyName="userGroupId"
			modelVar="userGroup"
		>

			<%
			String displayStyle = userGroupsDisplayContext.getDisplayStyle();

			boolean selectUserGroup = false;
			%>

			<%@ include file="/user_group_columns.jspf" %>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= userGroupsDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<portlet:actionURL name="addGroupUserGroups" var="addGroupUserGroupsURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= addGroupUserGroupsURL %>" cssClass="hide" name="addGroupUserGroupsFm">
	<aui:input name="tabs1" type="hidden" value="user-groups" />
</aui:form>

<portlet:actionURL name="addUserGroupGroupRole" var="addUserGroupGroupRoleURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= addUserGroupGroupRoleURL %>" cssClass="hide" name="addUserGroupGroupRoleFm">
	<aui:input name="tabs1" type="hidden" value="user-groups" />
	<aui:input name="userGroupId" type="hidden" />
</aui:form>

<portlet:actionURL name="unassignUserGroupGroupRole" var="unassignUserGroupGroupRoleURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= unassignUserGroupGroupRoleURL %>" cssClass="hide" name="unassignUserGroupGroupRoleFm">
	<aui:input name="tabs1" type="hidden" value="user-groups" />
	<aui:input name="userGroupId" type="hidden" />
</aui:form>