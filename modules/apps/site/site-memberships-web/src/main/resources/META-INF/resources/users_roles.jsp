<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
UserRolesDisplayContext userRolesDisplayContext = new UserRolesDisplayContext(request, renderRequest, renderResponse);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new UserRolesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, userRolesDisplayContext) %>"
/>

<aui:form cssClass="container-fluid portlet-site-memberships-assign-roles" name="fm">
	<liferay-site:membership-policy-error />

	<liferay-ui:search-container
		id="userGroupRoleRole"
		searchContainer="<%= userRolesDisplayContext.getRoleSearchSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Role"
			keyPropertyName="roleId"
			modelVar="role"
		>

			<%
			String displayStyle = userRolesDisplayContext.getDisplayStyle();
			%>

			<%@ include file="/role_columns.jspf" %>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= userRolesDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>