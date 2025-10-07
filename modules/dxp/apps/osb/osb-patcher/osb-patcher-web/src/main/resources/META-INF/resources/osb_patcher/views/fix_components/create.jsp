<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");
%>

<liferay-ui:header
	title="add-fix-component"
/>

<aui:model-context bean="<%= null %>" model="<%= PatcherFixComponent.class %>" />

<portlet:actionURL name="/patcher/add_fix_components" var="addPatcherFixComponentURL" />

<liferay-frontend:edit-form
	action="<%= addPatcherFixComponentURL %>"
	fluid="<%= true %>"
	method="post"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<liferay-frontend:edit-form-body>
		<aui:input name="name" type="text" />
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= redirect %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>