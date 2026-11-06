<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String backURL = ParamUtil.getString(request, "backURL");

PortletURL homeURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCPath(
	"/view.jsp"
).buildPortletURL();

if (Validator.isNull(backURL)) {
	backURL = homeURL.toString();
}

long userGroupId = ParamUtil.getLong(request, "userGroupId");

UserGroup userGroup = UserGroupServiceUtil.fetchUserGroup(userGroupId);

String displayStyle = ParamUtil.getString(request, "displayStyle");

if (Validator.isNull(displayStyle)) {
	displayStyle = portalPreferences.getValue(UserGroupsAdminPortletKeys.USER_GROUPS_ADMIN, "users-display-style", "list");
}
else {
	portalPreferences.setValue(UserGroupsAdminPortletKeys.USER_GROUPS_ADMIN, "users-display-style", displayStyle);

	request.setAttribute(WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);
}

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);

renderResponse.setTitle(userGroup.getName());

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "user-groups"), homeURL.toString());
PortalUtil.addPortletBreadcrumbEntry(request, userGroup.getName(), null);

EditUserGroupAssignmentsManagementToolbarDisplayContext editUserGroupAssignmentsManagementToolbarDisplayContext = new EditUserGroupAssignmentsManagementToolbarDisplayContext(request, renderRequest, renderResponse, displayStyle, "/edit_user_group_assignments.jsp");

LinkedHashMap<String, Object> userParams = new LinkedHashMap<String, Object>();

if (filterManageableOrganizations) {
	userParams.put("usersOrgsTree", user.getOrganizations());
}

userParams.put("usersUserGroups", Long.valueOf(userGroup.getUserGroupId()));

SearchContainer<User> searchContainer = editUserGroupAssignmentsManagementToolbarDisplayContext.getSearchContainer(userParams);
%>

<clay:management-toolbar
	actionDropdownItems="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getActionDropdownItems() %>"
	additionalProps="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getAdditionalProps() %>"
	clearResultsURL="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getClearResultsURL() %>"
	creationMenu="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getCreationMenu() %>"
	itemsTotal="<%= searchContainer.getTotal() %>"
	orderDropdownItems="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getOrderByDropdownItems() %>"
	propsTransformer="{EditUserGroupAssignmentsManagementToolbarPropsTransformer} from user-groups-admin-web"
	searchActionURL="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getSearchActionURL() %>"
	searchContainerId="users"
	searchFormName="searchFm"
	selectable="<%= true %>"
	showCreationMenu="<%= true %>"
	showSearch="<%= true %>"
	sortingOrder="<%= searchContainer.getOrderByType() %>"
	sortingURL="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getSortingURL() %>"
	viewTypeItems="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getViewTypeItems() %>"
/>

<aui:form action="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getPortletURL() %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="userGroupId" type="hidden" value="<%= userGroup.getUserGroupId() %>" />
	<aui:input name="deleteUserGroupIds" type="hidden" />
	<aui:input name="addUserIds" type="hidden" />
	<aui:input name="removeUserIds" type="hidden" />

	<div id="breadcrumb">
		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, true, true) %>"
		/>
	</div>

	<liferay-ui:search-container
		id="users"
		searchContainer="<%= searchContainer %>"
		var="userSearchContainer"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.User"
			escapedModel="<%= true %>"
			keyPropertyName="userId"
			modelVar="user2"
			rowIdProperty="screenName"
		>
			<c:choose>
				<c:when test='<%= displayStyle.equals("descriptive") %>'>
					<liferay-ui:search-container-column-text>
						<liferay-user:user-portrait
							userId="<%= user2.getUserId() %>"
						/>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<div class="h5"><%= user2.getFullName() %></div>

						<div class="h6 text-default">
							<span><%= user2.getScreenName() %></span>
						</div>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-jsp
						path="/user_action.jsp"
					/>
				</c:when>
				<c:when test='<%= displayStyle.equals("icon") %>'>
					<liferay-ui:search-container-column-text>
						<clay:user-card
							propsTransformer="{UserDropdownDefaultPropsTransformer} from user-groups-admin-web"
							userCard="<%= new UserVerticalCard(renderRequest, renderResponse, userSearchContainer.getRowChecker(), user2) %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:otherwise>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-minw-200 table-title"
						name="name"
						property="fullName"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-minw-200"
						name="screen-name"
						property="screenName"
					/>

					<liferay-ui:search-container-column-jsp
						path="/user_action.jsp"
					/>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= displayStyle %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>