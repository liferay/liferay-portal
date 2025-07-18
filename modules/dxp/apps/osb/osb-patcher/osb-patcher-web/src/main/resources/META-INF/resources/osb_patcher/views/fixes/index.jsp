<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherFixesDisplayContext patcherFixesDisplayContext = new PatcherFixesDisplayContext(request, renderRequest, renderResponse);
%>

<clay:navigation-bar
	navigationItems='<%= patcherDisplayContext.getNavigationItems("fixes") %>'
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new PatcherFixesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, patcherFixesDisplayContext.getSearchContainer()) %>"
/>

<liferay-ui:search-container
	searchContainer="<%= patcherFixesDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherFix"
		escapedModel="<%= true %>"
		keyProperty="patcherFixId"
		modelVar="patcherFix"
	>
		<liferay-ui:search-container-column-text
			cssClass="osb-patcher-search-container-column-text-icon"
		>
			<c:if test="<%= patcherFix.isObsolete() %>">
				<portlet:renderURL var="viewPatcherFixPopUpURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
					<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes" />
					<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
				</portlet:renderURL>

				<clay:button
					aria-label='<%= LanguageUtil.get(request, "this-fix-is-obsolete") %>'
					displayType="link"
					icon="view"
					onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.get(request, "view-fix") + "', '" + viewPatcherFixPopUpURL + "');" %>'
				/>
			</c:if>
		</liferay-ui:search-container-column-text>

		<portlet:renderURL var="viewPatcherFixURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes" />
			<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			href="<%= viewPatcherFixURL %>"
			name="fix-id"
			property="patcherFixId"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand-smallest table-cell-minw-150"
			name="content"
		>

			<%
			for (String token : StringUtil.split(patcherFix.getName())) {
			%>

				<c:choose>
					<c:when test="<%= PatcherFixPackUtil.containsPatcherFixPackName(token) %>">

						<%
						PatcherFixPack patcherFixPack = PatcherFixPackUtil.getPatcherFixPack(token, patcherFix.getPatcherProjectVersionId());
						%>

						<portlet:renderURL var="viewPatcherFixPackURL">
							<portlet:param name="mvcRenderCommandName" value="/patcher/view_fix_packs" />
							<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
						</portlet:renderURL>

						<a class="nobr" href="<%= viewPatcherFixPackURL %>"><%= token %></a>,
					</c:when>
					<c:otherwise>
						<a class="nobr" href="<%= patcherConfiguration.jiraURL() %>/<%= token %>" target="_blank"><%= token %></a>,
					</c:otherwise>
				</c:choose>

			<%
			}
			%>

		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<%
		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherFix.getPatcherProjectVersionId());
		%>

		<liferay-ui:search-container-column-text
			name="product-version"
			value="<%= PatcherProductVersionUtil.fetchPatcherProductVersionName(patcherProjectVersion.getPatcherProductVersionId()) %>"
		/>

		<liferay-ui:search-container-column-text
			name="project-version"
			value="<%= patcherProjectVersion.getName() %>"
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

		<portlet:renderURL var="editPatcherFixCommentsFieldURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcRenderCommandName" value="/patcher/edit_comments_field_fixes" />
			<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			name="engineer-comments"
		>
			<c:choose>
				<c:when test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherFix.getUserId()) %>">
					<clay:button
						displayType="link"
						label="<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherFix.getComments(), 75) %>"
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "edit-engineer-comments-for-fix-id-x", patcherFix.getPatcherFixId()) + "', '" + editPatcherFixCommentsFieldURL + "');" %>'
					/>
				</c:when>
				<c:otherwise>
					<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherFix.getComments(), 75) %>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="jenkins"
		>

			<%
			for (Map<String, String> jenkinsResult : JenkinsUtil.getJenkinsResults(patcherFix)) {
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
			name="type"
			value="<%= LanguageUtil.get(request, PatcherFixConstants.getTypeLabel(patcherFix.getType())) %>"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<clay:dropdown-actions
				aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
				dropdownItems="<%= patcherFixesDisplayContext.getDropdownItems(patcherFix) %>"
				propsTransformer="{PatcherDropdownDefaultPropsTransformer} from osb-patcher-web"
			/>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>

<aui:script>
	function <portlet:namespace />handleClick(title, url) {
		Liferay.Util.openModal({
			title: title,
			url: url,
		});
	}
</aui:script>