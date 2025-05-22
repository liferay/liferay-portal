<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-fixes"
	total="${fn:length(patcherFixes)}"
>
	<liferay-ui:search-container-results
		results="${patcherFixes}"
	/>

	<c:set value="${fn:length(patcherFixes)}" var="resultsTotal" />

	<%@ include file="/osb_patcher/views/show_results_count.jspf" %>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherFix"
		escapedModel="${true}"
		keyProperty="patcherFixId"
		modelVar="patcherFix"
	>
		<portlet:renderURL var="viewPatcherBuildPatcherFixesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="fixes" />
			<portlet:param name="id" value="${patcherBuild.patcherBuildId}" />
		</portlet:renderURL>

		<portlet:renderURL var="viewPatcherFixURL">
			<portlet:param name="controller" value="fixes" />
			<portlet:param name="action" value="view" />
			<portlet:param name="id" value="${patcherFix.patcherFixId}" />
			<portlet:param name="redirect" value="${viewPatcherBuildPatcherFixesURL}" />
		</portlet:renderURL>

		<c:set value='javascript:${renderResponse.namespace}navigateWindow("${viewPatcherFixURL}")' var="viewPatcherFixPopUpURL" />

		<liferay-ui:search-container-column-text>
			<c:if test="${patcherFix.obsolete}">
				<liferay-ui:icon
					image="../common/activate"
					message="this-fix-is-obsolete"
					url="${viewPatcherFixPopUpURL}"
				/>
			</c:if>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="fix-id"
		>
			<a class="clean-link" href="${viewPatcherFixURL}" onClick="event.preventDefault(); ${renderResponse.namespace}navigateWindow("${viewPatcherFixURL}");">${patcherFix.patcherFixId}</a>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="name"
		>
			<c:set value="${StringUtil.split(patcherFix.getName())}" var="jiraTickets" />

			<c:forEach items="${jiraTickets}" var="jiraTicket" varStatus="jiraTicketStatus">
				<a class="nobr" href="${PortletPropsValues.JIRA_URL}/${jiraTicket}" target="_blank">${jiraTicket}</a>${(!jiraTicketStatus.last) ? StringPool.COMMA : StringPool.BLANK}
			</c:forEach>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<liferay-ui:search-container-column-text
			href="${PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId())}"
			name="git-hash"
			target="_blank"
			value="${fn:substring(patcherFix.gitHash, 0, 10)}"
		/>

		<liferay-ui:search-container-column-text
			name="status"
			value="${AlloyLanguageUtil.format(WorkflowConstantsMethods.getStatusLabel(patcherFix.getStatus()))}"
		/>

		<portlet:renderURL var="editPatcherFixCommentsFieldURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="fixes" />
			<portlet:param name="action" value="editCommentsField" />
			<portlet:param name="id" value="${patcherFix.patcherFixId}" />
		</portlet:renderURL>

		<c:set value='${AlloyLanguageUtil.formatUnicode("edit-engineer-comments")}' var="editPatcherFixCommentsFieldURLTitle" />

		<c:set value="javascript:Liferay.Patcher.openWindow('${editPatcherFixCommentsFieldURL}', '${editPatcherFixCommentsFieldURLTitle}', true, 800)" var="editPatcherFixCommentsFieldURL" />

		<c:set value="${StringUtil.shorten(patcherFix.comments, 75)}" var="shortenedPatcherFixComments" />

		<liferay-ui:search-container-column-text
			name="engineer-comments"
		>
			<c:choose>
				<c:when test="${PatcherPermission.contains(themeDisplay, patcherFix, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherFix.userId) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK)}">
					<aui:a href="${editPatcherFixCommentsFieldURL}">
						${shortenedPatcherFixComments}
					</aui:a>
				</c:when>
				<c:otherwise>
					${shortenedPatcherFixComments}
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="type"
			value="${AlloyLanguageUtil.format(PatcherFixConstantsMethods.getTypeLabel(patcherFix.getType()))}"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:if test="${(patcherFix.status == WorkflowConstants.STATUS_FIX_FAILED) || (patcherFix.status == WorkflowConstants.STATUS_FIX_CONFLICT)}">
					<portlet:renderURL var="editPatcherFixURL">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="edit" />
						<portlet:param name="id" value="${patcherFix.patcherFixId}" />
						<portlet:param name="redirect" value="${viewPatcherBuildPatcherFixesURL}" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="${editPatcherFixURL}"
					/>
				</c:if>

				<c:if test='${PatcherPermissions.contains(themeDisplay, patcherFix, "exclude") && (patcherFix.type != PatcherFixConstants.TYPE_EXCLUDED)}'>
					<portlet:actionURL var="excludePatcherFixURL">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="exclude" />
						<portlet:param name="id" value="${patcherFix.patcherFixId}" />
						<portlet:param name="redirect" value="${viewPatcherBuildPatcherFixesURL}" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="../api/method"
						message="exclude"
						url="${excludePatcherFixURL}"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		paginate="${false}"
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