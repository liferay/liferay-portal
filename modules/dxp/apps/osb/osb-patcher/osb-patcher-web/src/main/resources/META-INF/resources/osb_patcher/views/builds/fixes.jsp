<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-fixes"
	total="<%= patcherFixes.size() %>"
>
	<liferay-ui:search-container-results
		results="<%= patcherFixes %>"
	/>

	<%
	int resultsTotal = patcherFixes.size();
	%>

	<%@ include file="/osb_patcher/views/show_results_count.jspf" %>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherFix"
		escapedModel="<%= true %>"
		keyProperty="patcherFixId"
		modelVar="patcherFix"
	>
		<portlet:renderURL var="viewPatcherBuildPatcherFixesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes_builds" />
			<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
		</portlet:renderURL>

		<portlet:renderURL var="viewPatcherFixURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes" />
			<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
			<portlet:param name="redirect" value="<%= viewPatcherBuildPatcherFixesURL %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text>
			<c:if test="<%= patcherFix.obsolete %>">
				<liferay-ui:icon
					image="../common/activate"
					message="this-fix-is-obsolete"
					onClick='<%= liferayPortletResponse.getNamespace() + "navigateWindow(" + viewPatcherFixURL + ")" %>'
					url="javascript:void(0);"
				/>
			</c:if>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="fix-id"
		>
			<a class="clean-link" href="<%= viewPatcherFixURL %>" onClick="event.preventDefault(); <%= renderResponse.namespace %>navigateWindow("<%= viewPatcherFixURL %>");"><%= patcherFix.patcherFixId %></a>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="name"
		>
			<c:set value="<%= StringUtil.split(patcherFix.getName()) %>" var="jiraTickets" />

			<c:forEach items="<%= jiraTickets %>" var="jiraTicket" varStatus="jiraTicketStatus">
				<a class="nobr" href="<%= patcherConfiguration.jiraURL() %>/<%= jiraTicket %>" target="_blank"><%= jiraTicket %></a>,
			</c:forEach>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<liferay-ui:search-container-column-text
			href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()) %>"
			name="git-hash"
			target="_blank"
			value="<%= fn:substring(patcherFix.gitHash, 0, 10) %>"
		/>

		<liferay-ui:search-container-column-text
			name="status"
			value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherFix.getStatus())) %>"
		/>

		<portlet:renderURL var="editPatcherFixCommentsFieldURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcRenderCommandName" value="/patcher/edit_comments_field_fixes" />
			<portlet:param name="patcherFixId" value="<%= patcherFix.getPatcherFixId() %>" />
		</portlet:renderURL>

		<c:set value='<%= UnicodeLanguageUtil.get(request, "edit-engineer-comments") %>' var="editPatcherFixCommentsFieldURLTitle" />

		<c:set value='<%= "javascript:Liferay.Patcher.openWindow('" + editPatcherFixCommentsFieldURL %>', '<%= editPatcherFixCommentsFieldURLTitle + "', true, 800)" %>' var="editPatcherFixCommentsFieldURL" />

		<c:set value="<%= StringUtil.shorten(patcherFix.comments, 75) %>" var="shortenedPatcherFixComments" />

		<liferay-ui:search-container-column-text
			name="engineer-comments"
		>
			<c:choose>
				<c:when test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherFix.userId) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<a href="<%= editPatcherFixCommentsFieldURL %>">
						<%= shortenedPatcherFixComments %>
					</a>
				</c:when>
				<c:otherwise>
					<%= shortenedPatcherFixComments %>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="type"
			value="<%= LanguageUtil.get(request, PatcherFixConstants.getTypeLabel(patcherFix.getType())) %>"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:if test="<%= (patcherFix.status == WorkflowConstants.STATUS_FIX_FAILED) || (patcherFix.status == WorkflowConstants.STATUS_FIX_CONFLICT) %>">
					<portlet:renderURL var="editPatcherFixURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/edit_fixes" />
						<portlet:param name="patcherFixId" value="<%= patcherFix.getPatcherFixId() %>" />
						<portlet:param name="redirect" value="<%= viewPatcherBuildPatcherFixesURL %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="<%= editPatcherFixURL %>"
					/>
				</c:if>

				<c:if test='<%= PatcherPermissions.contains(themeDisplay, patcherFix, "exclude") && (patcherFix.type != PatcherFixConstants.TYPE_EXCLUDED) %>'>
					<portlet:actionURL name="/patcher/exclude_fixes" var="excludePatcherFixURL">
						<portlet:param name="patcherFixId" value="<%= patcherFix.getPatcherFixId() %>" />
						<portlet:param name="redirect" value="<%= viewPatcherBuildPatcherFixesURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="../api/method"
						message="exclude"
						url="<%= excludePatcherFixURL %>"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
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