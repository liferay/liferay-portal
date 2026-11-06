<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewFlatUsersDisplayContext viewFlatUsersDisplayContext = ViewFlatUsersDisplayContextFactory.create(request, renderRequest, renderResponse);

if (!ParamUtil.getBoolean(renderRequest, "advancedSearch")) {
	currentURLObj.setParameter("status", String.valueOf(viewFlatUsersDisplayContext.getStatus()));
}

request.setAttribute(UsersAdminWebKeys.STATUS, viewFlatUsersDisplayContext.getStatus());

String displayStyle = viewFlatUsersDisplayContext.getDisplayStyle();
%>

<clay:management-toolbar
	itemsType="users"
	managementToolbarDisplayContext="<%= viewFlatUsersDisplayContext.getManagementToolbarDisplayContext() %>"
	propsTransformer="{ViewFlatOrganizationsAndUsersManagementToolbarPropsTransformer} from users-admin-web"
/>

<aui:form action="<%= currentURLObj.toString() %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "search();" %>'>
	<liferay-portlet:renderURLParams varImpl="portletURL" />
	<aui:input name="redirect" type="hidden" value="<%= currentURLObj.toString() %>" />
	<aui:input name="screenNavigationCategoryKey" type="hidden" value="<%= viewFlatUsersDisplayContext.getScreenNavigationCategoryKey() %>" />
	<aui:input name="usersListView" type="hidden" value="<%= viewFlatUsersDisplayContext.getUsersListView() %>" />

	<liferay-ui:error exception="<%= RequiredUserException.class %>" message="you-cannot-delete-or-deactivate-a-required-user" />

	<c:if test="<%= Validator.isNotNull(viewFlatUsersDisplayContext.getViewUsersRedirect()) %>">
		<aui:input name="viewUsersRedirect" type="hidden" value="<%= viewFlatUsersDisplayContext.getViewUsersRedirect() %>" />
	</c:if>

	<liferay-ui:search-container
		cssClass="users-search-container"
		searchContainer="<%= viewFlatUsersDisplayContext.getSearchContainer() %>"
		var="userSearchContainer"
	>
		<aui:input name="deleteUserIds" type="hidden" />
		<aui:input name="status" type="hidden" value="<%= viewFlatUsersDisplayContext.getStatus() %>" />

		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.User"
			escapedModel="<%= true %>"
			keyPropertyName="userId"
			modelVar="user2"
			rowIdProperty="userId"
		>
			<liferay-portlet:renderURL varImpl="rowURL">
				<portlet:param name="p_u_i_d" value="<%= String.valueOf(user2.getUserId()) %>" />
				<portlet:param name="mvcRenderCommandName" value="/users_admin/edit_user" />
				<portlet:param name="backURL" value="<%= currentURL %>" />
				<portlet:param name="screenNavigationCategoryKey" value="<%= UserScreenNavigationEntryConstants.CATEGORY_KEY_GENERAL %>" />
			</liferay-portlet:renderURL>

			<%
			if (!UserPermissionUtil.contains(permissionChecker, user2.getUserId(), ActionKeys.UPDATE)) {
				rowURL = null;
			}
			%>

			<%@ include file="/user/search_columns.jspf" %>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= displayStyle %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>