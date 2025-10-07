<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditRolesDisplayContext editRolesDisplayContext = new EditRolesDisplayContext(request, renderRequest);
%>

<liferay-ui:search-container
	searchContainer="<%= editRolesDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.Role"
		escapedModel="<%= true %>"
		keyProperty="roleId"
		modelVar="role"
	>
		<portlet:renderURL var="rowURL">
			<portlet:param name="redirect" value='<%= String.valueOf(request.getAttribute("edit_roles.jsp-redirect")) %>' />
			<portlet:param name="className" value='<%= String.valueOf(request.getAttribute("edit_roles.jsp-className")) %>' />
			<portlet:param name="groupId" value="<%= String.valueOf(editRolesDisplayContext.getGroupId()) %>" />
			<portlet:param name="roleId" value="<%= String.valueOf(role.getRoleId()) %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand-small table-cell-minw-200 table-title"
			href="<%= rowURL %>"
			name="title"
			value="<%= role.getTitle(locale) %>"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand-smaller table-cell-minw-150 table-cell-ws-nowrap"
			href="<%= rowURL %>"
			name="type"
			value="<%= LanguageUtil.get(request, role.getTypeLabel()) %>"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand table-cell-minw-300"
			href="<%= rowURL %>"
			name="description"
			value="<%= role.getDescription(locale) %>"
		/>

		<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-35914") %>'>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-smaller table-cell-minw-150 table-cell-ws-nowrap"
				name="status"
			>
				<clay:label
					displayType="<%= WorkflowConstants.getStatusStyle(role.getStatus()) %>"
					label="<%= WorkflowConstants.getStatusLabel(role.getStatus()) %>"
				/>
			</liferay-ui:search-container-column-text>
		</c:if>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>