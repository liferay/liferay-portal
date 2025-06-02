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
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_fix_packs" />
			<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			href="<%= viewPatcherFixPackURL %>"
			name="name"
			value="<%= patcherFixPack.getName() %>"
		/>

		<%
		PatcherFixComponent patcherFixComponent = PatcherFixComponentLocalServiceUtil.fetchPatcherFixComponent(patcherFixPack.getPatcherFixComponentId());
		%>

		<liferay-ui:search-container-column-text
			name="component"
			value="<%= patcherFixComponent.getName() %>"
		/>

		<liferay-ui:search-container-column-text
			name="version"
			value="<%= String.valueOf(patcherFixPack.getVersion()) %>"
		/>

		<%
		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherFixPack.getPatcherProjectVersionId());
		%>

		<liferay-ui:search-container-column-text
			name="project-version"
			value="<%= patcherProjectVersion.getName() %>"
		/>

		<liferay-ui:search-container-column-text
			name="status"
			value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherFixPack.getStatus())) %>"
		/>

		<liferay-ui:search-container-column-text
			name="qa-status"
			value="<%= LanguageUtil.get(request, PatcherBuildUtil.getQAStatusLabel(patcherFixPack.getPatcherBuildId())) %>"
		/>

		<liferay-ui:search-container-column-text
			name="released-date"
		>
			<fmt:formatDate
				value="<%= patcherFixPack.getReleasedDate() %>"
			/>
		</liferay-ui:search-container-column-text>

		<%
		List<String> newTickets = PatcherUtil.getNewTickets(patcherFixPack);
		%>

		<liferay-ui:search-container-column-text
			name="new-issues"
			value="<%= String.valueOf(newTickets.size()) %>"
		/>

		<%
		List<String> overriddenTickets = PatcherUtil.getOverriddenTickets(patcherFixPack);
		%>

		<liferay-ui:search-container-column-text
			name="overridden-issues"
			value="<%= String.valueOf(overriddenTickets.size()) %>"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:if test="<%= PatcherPermission.contains(themeDisplay, patcherFixPack, PatcherActionKeys.EDIT, patcherFixPack.getUserId()) %>">
					<portlet:renderURL var="editPatcherFixPackURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/edit_fix_packs" />
						<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="<%= editPatcherFixPackURL %>"
					/>
				</c:if>

				<%
				PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherFixPack.getPatcherBuildId());
				%>

				<c:if test="<%= (patcherBuild != null) && (patcherBuild.getStatus() == WorkflowConstants.STATUS_BUILD_COMPLETE) %>">
					<portlet:actionURL name="/patcher/test_builds" var="testPatcherFixPackURL">
						<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="test"
						method="get"
						url="<%= testPatcherFixPackURL %>"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>