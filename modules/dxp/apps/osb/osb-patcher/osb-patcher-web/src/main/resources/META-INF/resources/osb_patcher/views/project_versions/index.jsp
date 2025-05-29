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
			<portlet:renderURL var="viewPatcherProjectVersionFixedIssuesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
				<portlet:param name="controller" value="project_versions" />
				<portlet:param name="action" value="fixedIssues" />
				<portlet:param name="id" value="<%= patcherProjectVersion.getPatcherProjectVersionId() %>" />
			</portlet:renderURL>

			<c:set value='<%= UnicodeLanguageUtil.get(request, "fixed-issues") %>' var="viewPatcherProjectVersionFixedIssuesURLTitle" />

			<c:set value='<%= "javascript:Liferay.Patcher.openWindow('" + viewPatcherProjectVersionFixedIssuesURL %>', '<%= viewPatcherProjectVersionFixedIssuesURLTitle + "', true, 1000, 1);" %>' var="viewPatcherProjectVersionFixedIssuesURL" />

			<c:set value="<%= PatcherUtil.getTicketsCount(patcherProjectVersion.getFixedIssues()) %>" var="ticketsCount" />

			<c:set value='<%= ticketsCount %> <%= LanguageUtil.get(request, "tickets") %>' var="tickets" />

			<a class="nobr" href="<%= viewPatcherProjectVersionFixedIssuesURL %>" title="<%= patcherProjectVersionFixedIssuesCount %>"><%= ticketsCount > 0 ? tickets : "" %> </a>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:if test="<%= PatcherPermission.contains(themeDisplay, patcherProjectVersion, PatcherActionKeys.EDIT, patcherProductVersion.getUserId()) %>">
					<portlet:renderURL var="editPatcherProjectVersionURL">
						<portlet:param name="controller" value="project_versions" />
						<portlet:param name="action" value="edit" />
						<portlet:param name="id" value="<%= patcherProjectVersion.getPatcherProjectVersionId() %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="<%= editPatcherProjectVersionURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(themeDisplay, patcherProjectVersion, ActionKeys.DELETE, patcherProductVersion.getUserId()) %>">
					<portlet:actionURL var="deletePatcherProjectVersionURL">
						<portlet:param name="controller" value="project_versions" />
						<portlet:param name="action" value="delete" />
						<portlet:param name="id" value="<%= patcherProjectVersion.getPatcherProjectVersionId() %>" />
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