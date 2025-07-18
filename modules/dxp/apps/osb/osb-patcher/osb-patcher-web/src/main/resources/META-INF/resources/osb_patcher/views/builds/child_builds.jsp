<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherChildBuildsDisplayContext patcherChildBuildsDisplayContext = new PatcherChildBuildsDisplayContext(request, renderRequest, renderResponse);

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

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherBuild"
		escapedModel="<%= true %>"
		keyProperty="patcherBuildId"
		modelVar="childPatcherBuild"
	>
		<portlet:renderURL var="viewPatcherBuildURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds" />
			<portlet:param name="patcherBuildId" value="<%= String.valueOf(childPatcherBuild.getPatcherBuildId()) %>" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			name="build-id"
		>
			<clay:link
				href="javascript:void(0);"
				label="<%= String.valueOf(childPatcherBuild.getPatcherBuildId()) %>"
				onClick='<%= liferayPortletResponse.getNamespace() + "navigateWindow('" + viewPatcherBuildURL + "'); " %>'
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

		<%
		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(childPatcherBuild.getPatcherProjectVersionId());
		%>

		<liferay-ui:search-container-column-text
			name="project-version"
			value="<%= patcherProjectVersion.getName() %>"
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
					label='<%= jenkinsResult.get("jobName") %>'
					target="_blank"
				/>

			<%
			}
			%>

		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<clay:dropdown-actions
				aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
				dropdownItems="<%= patcherChildBuildsDisplayContext.getDropdownItems(childPatcherBuild) %>"
			/>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
		paginate="<%= false %>"
	/>
</liferay-ui:search-container>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />navigateWindow',
		function (targetURL) {
			window.location.href = targetURL;
		}
	);
</aui:script>