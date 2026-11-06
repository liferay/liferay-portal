<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
UserGroupRolesDisplayContext userGroupRolesDisplayContext = new UserGroupRolesDisplayContext(request, renderRequest, renderResponse);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new UserGroupRolesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, userGroupRolesDisplayContext) %>"
/>

<aui:form cssClass="container-fluid portlet-site-memberships-assign-roles" name="fm">
	<liferay-ui:search-container
		id="userGroupGroupRoleRole"
		searchContainer="<%= userGroupRolesDisplayContext.getRoleSearchSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Role"
			keyPropertyName="roleId"
			modelVar="role"
		>

			<%
			String displayStyle = userGroupRolesDisplayContext.getDisplayStyle();
			%>

			<%@ include file="/role_columns.jspf" %>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= userGroupRolesDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>