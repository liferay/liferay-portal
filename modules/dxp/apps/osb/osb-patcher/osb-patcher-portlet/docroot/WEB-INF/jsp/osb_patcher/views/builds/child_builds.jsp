<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/WEB-INF/jsp/osb_patcher/views/init.jsp" %>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-builds"
>
	<liferay-ui:search-container-results
		results="${childPatcherBuilds}"
		total="${fn:length(childPatcherBuilds)}"
	/>

	<c:set value="${fn:length(childPatcherBuilds)}" var="resultsTotal" />

	<%@ include file="/WEB-INF/jsp/osb_patcher/views/show_results_count.jspf" %>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherBuild"
		escapedModel="${true}"
		keyProperty="patcherBuildId"
		modelVar="childPatcherBuild"
	>
		<portlet:renderURL var="viewChildPatcherBuildsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="childBuilds" />
			<portlet:param name="id" value="${parentPatcherBuild.patcherBuildId}" />
		</portlet:renderURL>

		<portlet:renderURL var="viewPatcherBuildURL">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="view" />
			<portlet:param name="id" value="${childPatcherBuild.patcherBuildId}" />
			<portlet:param name="redirect" value="${viewChildPatcherBuildsURL}" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			name="build-id"
		>
			<a class="clean-link" href="${viewPatcherBuildURL}" onClick="event.preventDefault(); ${renderResponse.namespace}navigateWindow("${viewPatcherBuildURL}");">${childPatcherBuild.patcherBuildId}</a>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<liferay-ui:search-container-column-text
			name="name"
		>
			<c:set value="${StringUtil.split(childPatcherBuild.getName())}" var="jiraTickets" />

			<c:forEach items="${jiraTickets}" var="jiraTicket" varStatus="jiraTicketStatus">
				<a class="nobr" href="${PortletPropsValues.JIRA_URL}/${jiraTicket}" target="_blank">${jiraTicket}</a>${(!jiraTicketStatus.last) ? StringPool.COMMA : StringPool.BLANK}
			</c:forEach>
		</liferay-ui:search-container-column-text>

		<c:set value="${PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(childPatcherBuild.getPatcherProjectVersionId())}" var="patcherProjectVersion" />

		<liferay-ui:search-container-column-text
			name="project-version"
			value="${patcherProjectVersion.name}"
		/>

		<liferay-ui:search-container-column-text
			name="patcher-status"
			value="${AlloyLanguageUtil.format(WorkflowConstantsMethods.getStatusLabel(childPatcherBuild.getStatus()))}"
		/>

		<liferay-ui:search-container-column-text
			name="jenkins"
		>
			<c:set value="${JenkinsUtil.getJenkinsResults(childPatcherBuild)}" var="jenkinsResults" />

			<c:forEach items="${jenkinsResults}" var="jenkinsResult">
				<aui:a cssClass="nobr" href="${jenkinsResult.statusURL}" label="${jenkinsResult.jobName}" target="_blank" />
			</c:forEach>
		</liferay-ui:search-container-column-text>

		<c:set value="${PatcherFixLocalServiceUtil.getPatcherFix(childPatcherBuild.getPatcherFixId())}" var="patcherFix" />

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu
				cssClass="osb-patcher-icon-menu"
			>
				<portlet:renderURL var="viewPatcherBuildPatcherFixesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
					<portlet:param name="controller" value="builds" />
					<portlet:param name="action" value="fixes" />
					<portlet:param name="id" value="${childPatcherBuild.patcherBuildId}" />
				</portlet:renderURL>

				<c:set value='${AlloyLanguageUtil.formatUnicode("view-fixes-for-build-id-x", childPatcherBuild.patcherBuildId)}' var="viewPatcherFixesURLTitle" />

				<c:if test='${PatcherPermission.contains(themeDisplay, childPatcherBuild, "fixes")}'>
					<liferay-ui:icon
						image="view"
						message="view-fixes"
						method="get"
						url="${viewPatcherBuildPatcherFixesURL}"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		paginate="${false}"
	/>

	<%@ include file="/WEB-INF/jsp/osb_patcher/views/show_results_count.jspf" %>
</liferay-ui:search-container>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />navigateWindow',
		function(targetURL) {
			window.location.href = targetURL;
		}
	);

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