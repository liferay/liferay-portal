<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherProjectVersionsDisplayContext patcherProjectVersionsDisplayContext = new PatcherProjectVersionsDisplayContext(request, renderRequest, renderResponse);
%>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="project-versions" />
</liferay-util:include>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new PatcherProjectVersionsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, patcherProjectVersionsDisplayContext.getSearchContainer()) %>"
/>

<liferay-ui:search-container
	searchContainer="<%= patcherProjectVersionsDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherProjectVersion"
		escapedModel="<%= true %>"
		keyProperty="patcherProjectVersionId"
		modelVar="patcherProjectVersion"
	>
		<liferay-ui:search-container-column-text
			name="product-version"
			value="<%= PatcherProductVersionUtil.fetchPatcherProductVersionName(patcherProjectVersion.getPatcherProductVersionId()) %>"
		/>

		<liferay-ui:search-container-column-text
			property="name"
		/>

		<c:if test="<%= permissionChecker.isCompanyAdmin() %>">
			<liferay-ui:search-container-column-text
				name="combined-branch"
				property="combinedBranch"
			/>
		</c:if>

		<liferay-ui:search-container-column-text
			name="tag-name"
			property="committish"
		/>

		<liferay-ui:search-container-column-text
			name="repository-name"
			property="repositoryName"
		/>

		<liferay-ui:search-container-column-text
			name="root-project-version"
			value="<%= PatcherProjectVersionUtil.getRootPatcherProjectVersionName(patcherProjectVersion) %>"
		/>

		<liferay-ui:search-container-column-text
			name="fixed-issues"
		>

			<%
			int ticketsCount = PatcherUtil.getTicketsCount(patcherProjectVersion.getFixedIssues());
			%>

			<c:if test="<%= ticketsCount > 0 %>">
				<portlet:renderURL var="viewPatcherProjectVersionFixedIssuesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
					<portlet:param name="mvcRenderCommandName" value="/patcher/view_project_versions_fixed_issues" />
					<portlet:param name="patcherProjectVersionId" value="<%= String.valueOf(patcherProjectVersion.getPatcherProjectVersionId()) %>" />
				</portlet:renderURL>

				<clay:button
					displayType="link"
					label='<%= LanguageUtil.format(request, "x-tickets", ticketsCount) %>'
					onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + viewPatcherProjectVersionFixedIssuesURL + "');" %>'
				/>
			</c:if>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherProjectVersion, PatcherActionKeys.EDIT, patcherProjectVersion.getUserId()) %>">
					<portlet:renderURL var="editPatcherProjectVersionURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/edit_project_versions" />
						<portlet:param name="patcherProjectVersionId" value="<%= String.valueOf(patcherProjectVersion.getPatcherProjectVersionId()) %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="<%= editPatcherProjectVersionURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherProjectVersion, ActionKeys.DELETE, patcherProjectVersion.getUserId()) %>">
					<portlet:actionURL name="/patcher/delete_project_versions" var="deletePatcherProjectVersionURL">
						<portlet:param name="patcherProjectVersionId" value="<%= String.valueOf(patcherProjectVersion.getPatcherProjectVersionId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon-delete
						url="<%= deletePatcherProjectVersionURL %>"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>

<aui:script>
	function <portlet:namespace />handleClick(url) {
		Liferay.Util.openModal({
			title: '<liferay-ui:message key="tickets" />',
			url: url,
		});
	}
</aui:script>