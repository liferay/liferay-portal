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
	displayStyle = portalPreferences.getValue(UsersAdminPortletKeys.USERS_ADMIN, "display-style", "list");
}
else {
	portalPreferences.setValue(UsersAdminPortletKeys.USERS_ADMIN, "display-style", displayStyle);

	request.setAttribute(WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);
}

String usersListView = (String)request.getAttribute("view.jsp-usersListView");

PortletURL portletURL = PortletURLBuilder.create(
	(PortletURL)request.getAttribute("view.jsp-portletURL")
).setParameter(
	"displayStyle", displayStyle
).setParameter(
	"screenNavigationCategoryKey", UserScreenNavigationEntryConstants.CATEGORY_KEY_ORGANIZATIONS
).buildPortletURL();

String keywords = ParamUtil.getString(request, "keywords");

LinkedHashMap<String, Object> organizationParams = new LinkedHashMap<String, Object>();

boolean showList = true;

if (filterManageableOrganizations) {
	List<Organization> userOrganizations = user.getOrganizations(true);

	if (userOrganizations.isEmpty()) {
		showList = false;
	}
	else {
		organizationParams.put("organizationsTree", userOrganizations);
	}
}
%>

<liferay-ui:success key="userAdded" message="the-user-was-created-successfully" />

<c:choose>
	<c:when test="<%= showList %>">

		<%
		ViewOrganizationsManagementToolbarDisplayContext viewOrganizationsManagementToolbarDisplayContext = new ViewOrganizationsManagementToolbarDisplayContext(request, renderRequest, renderResponse, displayStyle);

		SearchContainer<Organization> searchContainer = viewOrganizationsManagementToolbarDisplayContext.getSearchContainer(organizationParams, filterManageableOrganizations);
		%>

		<clay:management-toolbar
			actionDropdownItems="<%= viewOrganizationsManagementToolbarDisplayContext.getActionDropdownItems() %>"
			clearResultsURL="<%= viewOrganizationsManagementToolbarDisplayContext.getClearResultsURL() %>"
			creationMenu="<%= viewOrganizationsManagementToolbarDisplayContext.getCreationMenu() %>"
			itemsTotal="<%= searchContainer.getTotal() %>"
			orderDropdownItems="<%= viewOrganizationsManagementToolbarDisplayContext.getOrderByDropdownItems() %>"
			propsTransformer="{ViewFlatOrganizationsAndUsersManagementToolbarPropsTransformer} from users-admin-web"
			searchActionURL="<%= viewOrganizationsManagementToolbarDisplayContext.getSearchActionURL() %>"
			searchContainerId="organizations"
			searchFormName="searchFm"
			selectable="<%= true %>"
			showCreationMenu="<%= viewOrganizationsManagementToolbarDisplayContext.showCreationMenu() %>"
			showSearch="<%= true %>"
			sortingOrder="<%= searchContainer.getOrderByType() %>"
			sortingURL="<%= viewOrganizationsManagementToolbarDisplayContext.getSortingURL() %>"
			viewTypeItems="<%= viewOrganizationsManagementToolbarDisplayContext.getViewTypeItems() %>"
		/>

		<aui:form action="<%= portletURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "search();" %>'>
			<liferay-portlet:renderURLParams varImpl="portletURL" />
			<aui:input name="<%= Constants.CMD %>" type="hidden" />
			<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />
			<aui:input name="screenNavigationCategoryKey" type="hidden" value="<%= UserScreenNavigationEntryConstants.CATEGORY_KEY_ORGANIZATIONS %>" />

			<liferay-ui:error exception="<%= RequiredOrganizationException.class %>" message="you-cannot-delete-organizations-that-have-suborganizations-or-users" />

			<liferay-ui:search-container
				id="organizations"
				iteratorURL="<%= portletURL %>"
				searchContainer="<%= searchContainer %>"
				var="organizationSearchContainer"
			>
				<aui:input name="deleteOrganizationIds" type="hidden" />

				<c:if test="<%= usersListView.equals(UserConstants.LIST_VIEW_FLAT_ORGANIZATIONS) %>">
					<div id="breadcrumb">
						<liferay-site-navigation:breadcrumb
							breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, true, true) %>"
						/>
					</div>
				</c:if>

				<liferay-ui:search-container-row
					className="com.liferay.portal.kernel.model.Organization"
					escapedModel="<%= true %>"
					keyPropertyName="organizationId"
					modelVar="organization"
				>
					<liferay-portlet:renderURL varImpl="rowURL">
						<portlet:param name="mvcRenderCommandName" value="/users_admin/organizations_view_tree" />
						<portlet:param name="redirect" value="<%= organizationSearchContainer.getIteratorURL().toString() %>" />
						<portlet:param name="organizationId" value="<%= String.valueOf(organization.getOrganizationId()) %>" />
						<portlet:param name="screenNavigationCategoryKey" value="<%= UserScreenNavigationEntryConstants.CATEGORY_KEY_ORGANIZATIONS %>" />
						<portlet:param name="usersListView" value="<%= UserConstants.LIST_VIEW_TREE %>" />
					</liferay-portlet:renderURL>

					<%
					if (!OrganizationPermissionUtil.contains(permissionChecker, organization, ActionKeys.VIEW)) {
						rowURL = null;
					}

					OrganizationActionDropdownItems organizationActionDropdownItems = new OrganizationActionDropdownItems(organization, renderRequest, renderResponse);
					%>

					<%@ include file="/organization/search_columns.jspf" %>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					displayStyle="<%= displayStyle %>"
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</aui:form>
	</c:when>
	<c:otherwise>
		<clay:alert
			message="you-do-not-belong-to-an-organization-and-are-not-allowed-to-view-other-organizations"
		/>
	</c:otherwise>
</c:choose>