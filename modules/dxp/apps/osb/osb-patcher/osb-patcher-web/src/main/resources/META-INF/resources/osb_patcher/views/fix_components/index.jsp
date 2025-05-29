<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherFixComponentsDisplayContext patcherFixComponentsDisplayContext = new PatcherFixComponentsDisplayContext(request, renderRequest, renderResponse);
%>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="fix-components" />
</liferay-util:include>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new PatcherFixComponentsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, patcherFixComponentsDisplayContext.getSearchContainer()) %>"
/>

<liferay-ui:search-container
	searchContainer="<%= patcherFixComponentsDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherFixComponent"
		escapedModel="<%= true %>"
		keyProperty="patcherFixComponentId"
		modelVar="patcherFixComponent"
	>
		<liferay-ui:search-container-column-text
			property="name"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:if test="<%= PatcherPermission.contains(themeDisplay, patcherFixComponent, PatcherActionKeys.EDIT, patcherFixComponent.getUserId()) %>">
					<portlet:renderURL var="editPatcherFixComponentURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/edit_fix_components" />
						<portlet:param name="patcherFixComponentId" value="<%= String.valueOf(patcherFixComponent.getPatcherFixComponentId()) %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="<%= editPatcherFixComponentURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(themeDisplay, patcherFixComponent, ActionKeys.DELETE, patcherFixComponent.getUserId()) %>">
					<portlet:actionURL name="/patcher/delete_fix_components" var="deletePatcherFixComponentURL">
						<portlet:param name="patcherFixComponentId" value="<%= String.valueOf(patcherFixComponent.getPatcherFixComponentId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon-delete
						url="<%= deletePatcherFixComponentURL %>"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>