<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

PasswordPolicy passwordPolicy = (PasswordPolicy)row.getObject();
%>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message="<%= StringPool.BLANK %>"
	showWhenSingleIcon="<%= true %>"
>
	<c:if test="<%= PasswordPolicyPermissionUtil.contains(permissionChecker, passwordPolicy.getPasswordPolicyId(), ActionKeys.UPDATE) %>">
		<portlet:renderURL var="editURL">
			<portlet:param name="mvcPath" value="/edit_password_policy.jsp" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="passwordPolicyId" value="<%= String.valueOf(passwordPolicy.getPasswordPolicyId()) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			message="edit"
			url="<%= editURL %>"
		/>
	</c:if>

	<c:if test="<%= PasswordPolicyPermissionUtil.contains(permissionChecker, passwordPolicy.getPasswordPolicyId(), ActionKeys.PERMISSIONS) %>">
		<liferay-security:permissionsURL
			modelResource="<%= PasswordPolicy.class.getName() %>"
			modelResourceDescription="<%= HtmlUtil.escape(passwordPolicy.getName()) %>"
			resourcePrimKey="<%= String.valueOf(passwordPolicy.getPasswordPolicyId()) %>"
			var="permissionsURL"
			windowState="<%= LiferayWindowState.POP_UP.toString() %>"
		/>

		<liferay-ui:icon
			message="permissions"
			method="get"
			url="<%= permissionsURL %>"
			useDialog="<%= true %>"
		/>
	</c:if>

	<c:if test="<%= PasswordPolicyPermissionUtil.contains(permissionChecker, passwordPolicy.getPasswordPolicyId(), ActionKeys.ASSIGN_MEMBERS) %>">
		<portlet:renderURL var="assignMembersURL">
			<portlet:param name="mvcPath" value="/edit_password_policy_assignments.jsp" />
			<portlet:param name="tabs1" value="assignees" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="passwordPolicyId" value="<%= String.valueOf(passwordPolicy.getPasswordPolicyId()) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			message="assign-members"
			url="<%= assignMembersURL %>"
		/>
	</c:if>

	<c:if test="<%= !FIPSUtil.isCryptoOfficerPasswordPolicy(passwordPolicy.getName()) && !passwordPolicy.getDefaultPolicy() && PasswordPolicyPermissionUtil.contains(permissionChecker, passwordPolicy.getPasswordPolicyId(), ActionKeys.DELETE) %>">
		<portlet:actionURL name="deletePasswordPolicy" var="deletePasswordPolicyURL">
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="passwordPolicyId" value="<%= String.valueOf(passwordPolicy.getPasswordPolicyId()) %>" />
		</portlet:actionURL>

		<liferay-ui:icon-delete
			url="<%= deletePasswordPolicyURL %>"
		/>
	</c:if>
</liferay-ui:icon-menu>