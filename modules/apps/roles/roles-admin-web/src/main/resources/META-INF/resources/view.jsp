<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String displayStyle = ParamUtil.getString(request, "displayStyle");

if (Validator.isNull(displayStyle)) {
	displayStyle = portalPreferences.getValue(RolesAdminPortletKeys.ROLES_ADMIN, "displayStyle", "descriptive");
}
else {
	portalPreferences.setValue(RolesAdminPortletKeys.ROLES_ADMIN, "displayStyle", displayStyle);

	request.setAttribute(WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);
}

ViewRolesManagementToolbarDisplayContext viewRolesManagementToolbarDisplayContext = new ViewRolesManagementToolbarDisplayContext(request, renderRequest, renderResponse, displayStyle);

SearchContainer<Role> searchContainer = viewRolesManagementToolbarDisplayContext.getSearchContainer();

PortletURL portletURL = viewRolesManagementToolbarDisplayContext.getPortletURL();
%>

<clay:navigation-bar
	navigationItems="<%= roleDisplayContext.getViewRoleNavigationItems(liferayPortletResponse, portletURL) %>"
/>

<portlet:actionURL name="deleteRoles" var="deleteRolesURL">
	<portlet:param name="redirect" value="<%= portletURL.toString() %>" />
</portlet:actionURL>

<clay:management-toolbar
	actionDropdownItems="<%= viewRolesManagementToolbarDisplayContext.getActionDropdownItems() %>"
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"deleteRolesURL", deleteRolesURL.toString()
		).build()
	%>'
	clearResultsURL="<%= viewRolesManagementToolbarDisplayContext.getClearResultsURL() %>"
	creationMenu="<%= viewRolesManagementToolbarDisplayContext.getCreationMenu() %>"
	filterDropdownItems="<%= viewRolesManagementToolbarDisplayContext.getFilterItemsDropdownItems() %>"
	filterLabelItems="<%= viewRolesManagementToolbarDisplayContext.getFilterLabelItems() %>"
	itemsTotal="<%= searchContainer.getTotal() %>"
	orderDropdownItems="<%= viewRolesManagementToolbarDisplayContext.getOrderByDropdownItems() %>"
	propsTransformer="{ViewRolesManagementToolbarPropsTransformer} from roles-admin-web"
	searchActionURL="<%= viewRolesManagementToolbarDisplayContext.getSearchActionURL() %>"
	searchContainerId="roleSearch"
	searchFormName="searchFm"
	selectable="<%= true %>"
	showCreationMenu="<%= viewRolesManagementToolbarDisplayContext.showCreationMenu() %>"
	showSearch="<%= true %>"
	sortingOrder="<%= searchContainer.getOrderByType() %>"
	sortingURL="<%= viewRolesManagementToolbarDisplayContext.getSortingURL() %>"
	viewTypeItems="<%= viewRolesManagementToolbarDisplayContext.getViewTypeItems() %>"
/>

<aui:form action="<%= portletURL %>" cssClass="container-fluid container-fluid-max-xl container-form-view" method="get" name="fm">
	<liferay-ui:error exception="<%= RequiredRoleException.class %>" message="you-cannot-delete-a-system-role" />

	<aui:input name="deleteRoleIds" type="hidden" />

	<liferay-ui:search-container
		id="roleSearch"
		searchContainer="<%= searchContainer %>"
		var="roleSearchContainer"
	>
		<aui:input name="rolesRedirect" type="hidden" value="<%= portletURL.toString() %>" />

		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Role"
			keyProperty="roleId"
			modelVar="role"
		>

			<%
			PortletURL rowURL = null;

			if (RolePermissionUtil.contains(permissionChecker, role.getRoleId(), ActionKeys.UPDATE)) {
				PortletURL searchContainerPortletURL = roleSearchContainer.getIteratorURL();

				rowURL = PortletURLBuilder.createRenderURL(
					renderResponse
				).setMVCPath(
					"/edit_role.jsp"
				).setBackURL(
					searchContainerPortletURL.toString()
				).setTabs1(
					"details"
				).setParameter(
					"roleId", role.getRoleId()
				).buildPortletURL();
			}
			%>

			<%@ include file="/search_columns.jspf" %>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= displayStyle %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>