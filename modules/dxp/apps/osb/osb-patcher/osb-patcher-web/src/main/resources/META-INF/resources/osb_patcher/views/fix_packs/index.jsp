<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherFixPacksDisplayContext patcherFixPacksDisplayContext = new PatcherFixPacksDisplayContext(request, renderRequest, renderResponse);
%>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="fix-packs" />
</liferay-util:include>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new PatcherFixPacksManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, patcherFixPacksDisplayContext.getSearchContainer()) %>"
/>

<liferay-ui:search-container
	searchContainer="<%= patcherFixPacksDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherFixPack"
		escapedModel="<%= true %>"
		keyProperty="patcherFixPackId"
		modelVar="patcherFixPack"
	>
		<portlet:renderURL var="viewPatcherFixPackURL">
			<portlet:param name="controller" value="fix_packs" />
			<portlet:param name="action" value="view" />
			<portlet:param name="id" value="<%= patcherFixPack.patcherFixPackId %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			href="<%= viewPatcherFixPackURL %>"
			name="name"
			value="<%= patcherFixPack.name %>"
		/>

		<c:set value="<%= PatcherFixComponentLocalServiceUtil.fetchPatcherFixComponent(patcherFixPack.getPatcherFixComponentId()) %>" var="patcherFixComponent" />

		<liferay-ui:search-container-column-text
			name="component"
			value="<%= patcherFixComponent.name %>"
		/>

		<liferay-ui:search-container-column-text
			name="version"
			value="<%= patcherFixPack.version %>"
		/>

		<c:set value="<%= PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherFixPack.getPatcherProjectVersionId()) %>" var="patcherProjectVersion" />

		<liferay-ui:search-container-column-text
			name="project-version"
			value="<%= patcherProjectVersion.name %>"
		/>

		<liferay-ui:search-container-column-text
			name="status"
			value='<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherFixPack.getStatus())) + ">" %>'
		/>

		<liferay-ui:search-container-column-text
			name="qa-status"
			value="<%= LanguageUtil.get(request, PatcherBuildUtil.getQAStatusLabel(patcherFixPack.getPatcherBuildId())) %>"
		/>

		<liferay-ui:search-container-column-text
			name="released-date"
		>
			<fmt:formatDate
				value="<%= patcherFixPack.releasedDate %>"
			/>
		</liferay-ui:search-container-column-text>

		<c:set value="<%= PatcherUtil.getNewTickets(patcherFixPack) %>" var="newTickets" />

		<liferay-ui:search-container-column-text
			name="new-issues"
			value="<%= fn:length(newTickets) %>"
		/>

		<c:set value="<%= PatcherUtil.getOverriddenTickets(patcherFixPack) %>" var="overriddenTickets" />

		<liferay-ui:search-container-column-text
			name="overridden-issues"
			value="<%= fn:length(overriddenTickets) %>"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:if test="<%= PatcherPermission.contains(themeDisplay, patcherFixPack, PatcherActionKeys.EDIT, patcherFixPack.userId) %>">
					<portlet:renderURL var="editPatcherFixPackURL">
						<portlet:param name="controller" value="fix_packs" />
						<portlet:param name="action" value="edit" />
						<portlet:param name="id" value="<%= patcherFixPack.patcherFixPackId %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="<%= editPatcherFixPackURL %>"
					/>
				</c:if>

				<c:if test="<%= patcherBuild.status == WorkflowConstants.STATUS_BUILD_COMPLETE %>">
					<portlet:actionURL var="testPatcherFixPackURL">
						<portlet:param name="controller" value="builds" />
						<portlet:param name="action" value="test" />
						<portlet:param name="id" value="<%= patcherFixPack.patcherBuildId %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="test"
						method="get"
						url="<%= testPatcherFixPackURL %>"
					/>
				</c:if>

				<c:if test="<%= false %>">
					<portlet:actionURL var="deletePatcherFixPackURL">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="delete" />
						<portlet:param name="id" value="<%= patcherFixPack.patcherFixPackId %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon-delete
						url="<%= deletePatcherFixPackURL %>"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>