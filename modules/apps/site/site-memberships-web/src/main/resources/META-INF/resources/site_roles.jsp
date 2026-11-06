<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
RolesDisplayContext rolesDisplayContext = new RolesDisplayContext(request, renderRequest, renderResponse);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new RolesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, rolesDisplayContext) %>"
/>

<aui:form cssClass="container-fluid portlet-site-memberships-assign-roles" name="fm">
	<liferay-ui:search-container
		id="roles"
		searchContainer="<%= rolesDisplayContext.getRoleSearchSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Role"
			escapedModel="<%= true %>"
			keyPropertyName="roleId"
			modelVar="role"
		>

			<%
			String displayStyle = rolesDisplayContext.getDisplayStyle();
			%>

			<%@ include file="/role_columns.jspf" %>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= rolesDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>