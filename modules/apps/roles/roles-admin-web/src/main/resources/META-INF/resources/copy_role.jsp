<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<portlet:actionURL name="copyRole" var="copyRoleURL" />

<aui:form action="<%= copyRoleURL %>" cssClass="p-4" method="post" name="fm">
	<aui:input name="backURL" type="hidden" value='<%= ParamUtil.getString(request, "backURL") %>' />
	<aui:input name="roleId" type="hidden" value='<%= ParamUtil.getLong(request, "roleId") %>' />

	<liferay-ui:error exception="<%= DuplicateRoleException.class %>" message="please-enter-a-unique-name" />
	<liferay-ui:error exception="<%= RoleNameException.class %>" message="please-enter-a-valid-name" />

	<aui:input label="new-role-name" name="roleName">
		<aui:validator errorMessage="please-enter-a-valid-name" name="required" />
	</aui:input>

	<aui:button-row>
		<aui:button type="submit" />

		<aui:button onClick="window.close();" type="cancel" />
	</aui:button-row>
</aui:form>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"redirectURL", ParamUtil.getString(request, "redirectURL")
		).build()
	%>'
	module="{copyRoleRedirect} from roles-admin-web"
/>