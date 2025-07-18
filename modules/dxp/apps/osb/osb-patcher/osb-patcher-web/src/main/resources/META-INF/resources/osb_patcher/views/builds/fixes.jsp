<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherBuildFixesDisplayContext patcherBuildFixesDisplayContext = new PatcherBuildFixesDisplayContext(request, renderRequest, renderResponse);

long patcherBuildId = ParamUtil.getLong(request, "patcherBuildId");

PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherBuildId);

List<PatcherFix> patcherFixes = PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixes(patcherBuild.getPatcherBuildId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS, PatcherFixStatusComparator.getInstance(false));

patcherFixes = ListUtil.copy(patcherFixes);

PatcherFix mainPatcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(patcherBuild.getPatcherFixId());

if ((mainPatcherFix != null) && (mainPatcherFix.getStatus() == WorkflowConstants.STATUS_FIX_CONFLICT)) {
	if (PatcherFixPackUtil.containsPatcherFixPackName(mainPatcherFix.getName())) {
		patcherFixes.remove(mainPatcherFix);
	}
	else {
		for (PatcherFix patcherFix : patcherFixes) {
			if (!patcherFix.equals(mainPatcherFix) && ((patcherFix.getStatus() == WorkflowConstants.STATUS_FIX_CONFLICT) || (patcherFix.getStatus() == WorkflowConstants.STATUS_FIX_ADDING))) {
				patcherFixes.remove(mainPatcherFix);

				break;
			}
		}
	}
}
else if (patcherFixes.size() > 1) {
	patcherFixes.remove(mainPatcherFix);
}
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
					aria-label='<%= LanguageUtil.get(request, "this-fix-is-obsolete") %>'
					cssClass="lfr-portal-tooltip"
					href="javascript:void(0);"
					icon="check-circle"
					onClick='<%= liferayPortletResponse.getNamespace() + "navigateWindow('" + viewPatcherFixURL + "')" %>'
					title='<%= LanguageUtil.get(request, "this-fix-is-obsolete") %>'
				/>
			</c:if>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="fix-id"
		>
			<clay:link
				href="javascript:void(0);"
				label="<%= String.valueOf(patcherFix.getPatcherFixId()) %>"
				onClick='<%= liferayPortletResponse.getNamespace() + "navigateWindow('" + viewPatcherFixURL + "'); " %>'
			/>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="name"
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
			name="status"
			value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherFix.getStatus())) %>"
		/>

		<portlet:renderURL var="editPatcherFixCommentsFieldURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcRenderCommandName" value="/patcher/edit_comments_field_fixes" />
			<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			name="engineer-comments"
		>
			<c:choose>
				<c:when test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherFix.getUserId()) && (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<clay:button
						displayType="link"
						label="<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherFix.getComments(), 75) %>"
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.get(request, "edit-engineer-comments") + "', '" + editPatcherFixCommentsFieldURL + "');" %>'
					/>
				</c:when>
				<c:otherwise>
					<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherFix.getComments(), 75) %>
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
			<clay:dropdown-actions
				aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
				dropdownItems="<%= patcherBuildFixesDisplayContext.getDropdownItems(patcherFix) %>"
				propsTransformer="{PatcherDropdownDefaultPropsTransformer} from osb-patcher-web"
			/>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
		paginate="<%= false %>"
	/>
</liferay-ui:search-container>

<aui:script>
	function <portlet:namespace />handleClick(title, url) {
		Liferay.Util.openModal({
			title: title,
			url: url,
		});
	}

	function <portlet:namespace />navigateWindow(targetURL) {
		window.location.href = targetURL;
	}
</aui:script>