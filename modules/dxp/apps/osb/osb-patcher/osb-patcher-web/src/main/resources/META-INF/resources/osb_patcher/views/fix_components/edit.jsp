<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

long patcherFixComponentId = ParamUtil.getLong(request, "patcherFixComponentId");

PatcherFixComponent patcherFixComponent = PatcherFixComponentLocalServiceUtil.fetchPatcherFixComponent(patcherFixComponentId);
%>

<liferay-ui:header
	title='<%= LanguageUtil.format(request, "edit-x", String.valueOf(patcherFixComponent.getName())) %>'
/>

<aui:model-context bean="<%= patcherFixComponent %>" model="<%= PatcherFixComponent.class %>" />

<portlet:actionURL name="/patcher/update_fix_components" var="updatePatcherFixComponentURL" />

<liferay-frontend:edit-form
	action="<%= updatePatcherFixComponentURL %>"
	fluid="<%= true %>"
	method="post"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="patcherFixComponentId" type="hidden" value="<%= patcherFixComponent.getPatcherFixComponentId() %>" />

	<liferay-frontend:edit-form-body>
		<aui:input name="name" type="text" value="<%= patcherFixComponent.getName() %>" />
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= redirect %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>