<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
long patcherBuildId = ParamUtil.getLong(request, "patcherBuildId");

PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherBuildId);

List<PatcherBuild> childPatcherBuilds = PatcherBuildRelUtil.getChildPatcherBuilds(patcherBuild);
%>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-builds"
	total="<%= childPatcherBuilds.size() %>"
>
	<liferay-ui:search-container-results
		results="<%= childPatcherBuilds %>"
	/>

	<%
	int resultsTotal = childPatcherBuilds.size();
	%>

	<%@ include file="/osb_patcher/views/show_results_count.jspf" %>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherBuild"
		escapedModel="<%= true %>"
		keyProperty="patcherBuildId"
		modelVar="childPatcherBuild"
	>
		<portlet:renderURL var="viewChildPatcherBuildsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_child_builds_builds" />
			<portlet:param name="patcherBuildId" value="<%= String.valueOf(parentPatcherBuild.getPatcherBuildId()) %>" />
		</portlet:renderURL>

		<portlet:renderURL var="viewPatcherBuildURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds" />
			<portlet:param name="patcherBuildId" value="<%= String.valueOf(childPatcherBuild.getPatcherBuildId()) %>" />
			<portlet:param name="redirect" value="<%= viewChildPatcherBuildsURL %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			name="build-id"
		>
			<clay:link
				cssClass="clean-link"
				href="javascript:void(0);"
				label="<%= String.valueOf(childPatcherBuild.getPatcherBuildId()) %>"
				onClick='<%= liferayPortletResponse.getNamespace() + "navigateWindow(" + viewPatcherBuildURL + "); " %>'
			/>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<liferay-ui:search-container-column-text
			name="name"
		>

			<%
			for (String jiraTicket : StringUtil.split(childPatcherBuild.getName())) {
			%>

				<a class="nobr" href="<%= patcherConfiguration.jiraURL() %>/<%= jiraTicket %>" target="_blank"><%= jiraTicket %></a>,

			<%
			}
			%>

		</liferay-ui:search-container-column-text>

		<c:set value="<%= PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(childPatcherBuild.getPatcherProjectVersionId()) %>" var="patcherProjectVersion" />

		<liferay-ui:search-container-column-text
			name="project-version"
			value="<%= patcherProjectVersion.name %>"
		/>

		<liferay-ui:search-container-column-text
			name="patcher-status"
			value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(childPatcherBuild.getStatus())) %>"
		/>

		<liferay-ui:search-container-column-text
			name="jenkins"
		>

			<%
			for (Map<String, String> jenkinsResult : JenkinsUtil.getJenkinsResults(childPatcherBuild)) {
			%>

				<clay:link
					cssClass="nobr"
					href='<%= jenkinsResult.get("statusURL") %>'
					target="_blank"
					title='<%= jenkinsResult.get("jobName") %>'
				/>

			<%
			}
			%>

		</liferay-ui:search-container-column-text>

		<%
		PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(childPatcherBuild.getPatcherFixId());
		%>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu
				cssClass="osb-patcher-icon-menu"
			>
				<portlet:renderURL var="viewPatcherBuildPatcherFixesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
					<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes_builds" />
					<portlet:param name="patcherBuildId" value="<%= String.valueOf(childPatcherBuild.getPatcherBuildId()) %>" />
				</portlet:renderURL>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, childPatcherBuild, PatcherActionKeys.FIXES, childPatcherBuild.getUserId()) %>">
					<liferay-ui:icon
						image="view"
						message="view-fixes"
						method="get"
						url="<%= viewPatcherBuildPatcherFixesURL %>"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		paginate="<%= false %>"
	/>

	<%@ include file="/osb_patcher/views/show_results_count.jspf" %>
</liferay-ui:search-container>

<aui:script>
	function <portlet:namespace />navigateWindow(targetURL) {
		window.location.href = targetURL;
	}

	AUI().ready(
		function() {
			var A = AUI();

			var cleanLinks = A.all('.clean-link');

			cleanLinks.each(
				function(cleanLink) {
					var href = cleanLink.attr('href');

					var index = href.indexOf('?');

					cleanLink.set('href', href.substring(0, index));
				}
			);
		}
	);
</aui:script>