<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
long patcherFixId = ParamUtil.getLong(request, "patcherFixId");

PatcherFix childPatcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(patcherFixId);

List<PatcherFix> patcherFixes = PatcherFixUtil.getParentPatcherFixes(childPatcherFix);
%>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-fixes"
	total="<%= patcherFixes.size() %>"
>
	<liferay-ui:search-container-results
		results="<%= patcherFixes %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherFix"
		escapedModel="<%= true %>"
		keyProperty="patcherFixId"
		modelVar="patcherFix"
	>
		<portlet:renderURL var="viewPatcherFixURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes" />
			<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text>
			<c:if test="<%= patcherFix.isObsolete() %>">
				<clay:link
					href="<%= viewPatcherFixURL %>"
					icon="warning"
					title='<%= LanguageUtil.get(request, "this-fix-is-obsolete") %>'
				/>
			</c:if>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			href="<%= viewPatcherFixURL %>"
			name="fix-id"
			value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>"
		/>

		<liferay-ui:search-container-column-text
			name="content"
		>

			<%
			for (String jiraTicket : StringUtil.split(patcherFix.getName())) {
			%>

				<a class="nobr" href="<%= patcherConfiguration.jiraURL() %>/<%= jiraTicket %>" target="_blank"><%= jiraTicket %></a>,

			<%
			}
			%>

		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<liferay-ui:search-container-column-text
			href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()) %>"
			name="git-hash"
			target="_blank"
			value="<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherFix.getGitHash(), 10) %>"
		/>

		<liferay-ui:search-container-column-text
			name="patcher-status"
			value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherFix.getStatus())) %>"
		/>

		<liferay-ui:search-container-column-text
			name="engineer-comments"
			value="<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherFix.getComments(), 75) %>"
		/>

		<liferay-ui:search-container-column-text
			name="type"
			value="<%= LanguageUtil.get(request, PatcherFixConstants.getTypeLabel(patcherFix.getType())) %>"
		/>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
		paginate="<%= false %>"
	/>
</liferay-ui:search-container>