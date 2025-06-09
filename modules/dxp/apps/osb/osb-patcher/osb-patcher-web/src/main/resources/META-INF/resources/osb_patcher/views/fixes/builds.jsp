<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-builds"
	total="<%= fn:length(patcherBuilds) %>"
>
	<liferay-ui:search-container-results
		results="<%= patcherBuilds %>"
	/>

	<c:set value="<%= fn:length(patcherBuilds) %>" var="resultsTotal" />

	<%@ include file="/osb_patcher/views/show_results_count.jspf" %>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherBuild"
		escapedModel="<%= true %>"
		keyProperty="patcherBuildId"
		modelVar="patcherBuild"
	>
		<portlet:renderURL var="viewPatcherFixPatcherBuildsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="fixes" />
			<portlet:param name="action" value="builds" />
			<portlet:param name="id" value="<%= patcherFix.patcherFixId %>" />
		</portlet:renderURL>

		<portlet:renderURL var="viewPatcherBuildPatcherFixesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="fixes" />
			<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
			<portlet:param name="redirect" value="<%= viewPatcherFixPatcherBuildsURL %>" />
		</portlet:renderURL>

		<c:set value='<%= "javascript:" + renderResponse.namespace %>navigateWindow("<%= viewPatcherBuildPatcherFixesURL + ""); " %>' var="viewPatcherBuildPatcherFixesPopUpURL" />

		<c:set value="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.FIXES, patcherBuild.userId) %>" var="fixes" />

		<liferay-ui:search-container-column-text>
			<c:if test="<%= PatcherBuildUtil.isObsolete(patcherBuild.patcherBuildId) %>">
				<liferay-ui:icon
					image="../common/activate"
					message="this-build-is-obsolete"
					url="<%= fixes ? viewPatcherBuildPatcherFixesPopUpURL : StringPool.BLANK %>"
				/>
			</c:if>

			<liferay-ui:icon
				image='<%= PatcherFixUtil.containsPatcherFixWorkaround(patcherBuild.patcherBuildId) ? "../api/exception" : StringPool.BLANK %>'
				message="this-build-contains-workaround-fixes"
			/>
		</liferay-ui:search-container-column-text>

		<portlet:renderURL var="viewPatcherBuildURL">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="view" />
			<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
			<portlet:param name="redirect" value="<%= viewPatcherFixPatcherBuildsURL %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			name="build-id"
		>
			<a class="clean-link" href="<%= viewPatcherBuildURL %>" onClick="event.preventDefault(); <%= renderResponse.namespace %>navigateWindow("<%= viewPatcherBuildURL %>");"><%= patcherBuild.patcherBuildId %></a>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			cssClass="nobr"
			href='<%= patcherBuild.fileName.contains("/liferay-dxp-") ? "https://releases-cdn.liferay.com/dxp/hotfix" : patcherConfiguration.patcherBuildDownloadURL() %>/<%= patcherBuild.fileName %>'
			name="hotfix"
			target="_blank"
			value="<%= PatcherBuildUtil.isCompleteReadyOrReleased(patcherBuild) ? PatcherBuildUtil.getLiferayHotfixFileName(patcherBuild.fileName) : StringPool.BLANK %>"
		/>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<liferay-ui:search-container-column-text
			name="account-code"
			property="accountEntryCode"
		/>

		<liferay-ui:search-container-column-text
			cssClass="nobr"
			href="<%= PatcherBuildUtil.getSupportTicketURL(patcherBuild.supportTicket) %>"
			name="support-ticket"
			target="_blank"
			value="<%= patcherBuild.supportTicket %>"
		/>

		<c:set value="<%= PatcherFixLocalServiceUtil.fetchPatcherFix(patcherBuild.getPatcherFixId()) %>" var="patcherFix" />

		<liferay-ui:search-container-column-text
			href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherBuild.getPatcherFixId()) %>"
			name="git-hash"
			target="_blank"
			value="<%= fn:substring(patcherFix.gitHash, 0, 10) %>"
		/>

		<liferay-ui:search-container-column-text
			name="status"
			value='<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherBuild.getStatus())) + ">" %>'
		/>

		<liferay-ui:search-container-column-text
			name="jenkins"
		>
			<c:set value="<%= JenkinsUtil.getJenkinsResults(patcherBuild) %>" var="jenkinsResults" />

			<c:forEach items="<%= jenkinsResults %>" var="jenkinsResult">
				<clay:link
					cssClass="nobr"
					href="<%= jenkinsResult.statusURL %>"
					target="_blank"
					title="<%= jenkinsResult.jobName %>"
				/>
			</c:forEach>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="engineer-comments"
			property="comments"
		/>

		<liferay-ui:search-container-column-text
			name="qa-status"
			value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherBuild.getQaStatus())) %>"
		/>

		<liferay-ui:search-container-column-text
			name="qa-comments"
			property="qaComments"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:set value="<%= JenkinsUtil.isValidJenkinsSetup() %>" var="isValidJenkinsSetup" />

				<c:set value="<%= JenkinsUtil.isValidSendDistJenkinsRequest(themeDisplay, patcherBuild) %>" var="isValidSendDistJenkinsRequest" />

				<c:if test="<%= (not empty patcherFix.gitHash) && isValidJenkinsSetup && isValidSendDistJenkinsRequest %>">
					<portlet:actionURL var="buildPatcherBuildURL">
						<portlet:param name="controller" value="builds" />
						<portlet:param name="action" value="build" />
						<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
						<portlet:param name="redirect" value="<%= viewPatcherBuildsURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="build"
						method="get"
						url="<%= buildPatcherBuildURL %>"
					/>
				</c:if>

				<c:if test="<%= patcherBuild.status == WorkflowConstants.STATUS_BUILD_COMPLETE %>">
					<portlet:actionURL var="testPatcherBuildURL">
						<portlet:param name="controller" value="builds" />
						<portlet:param name="action" value="test" />
						<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
						<portlet:param name="redirect" value="<%= viewPatcherBuildsURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="test"
						method="get"
						url="<%= testPatcherBuildURL %>"
					/>

					<portlet:actionURL var="smokeTestPatcherBuildURL">
						<portlet:param name="controller" value="builds" />
						<portlet:param name="action" value="smokeTest" />
						<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
						<portlet:param name="redirect" value="<%= viewPatcherBuildsURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="smoke-test"
						method="get"
						url="<%= smokeTestPatcherBuildURL %>"
					/>

					<c:set value='<%= patcherBuild.fileName.contains("/liferay-dxp-") ? "https://releases-cdn.liferay.com/dxp/hotfix" : patcherConfiguration.patcherBuildDownloadURL() %>/<%= patcherBuild.fileName %>' var="hotfixURL" />

					<liferay-ui:icon
						image="download"
						url="<%= hotfixURL %>"
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