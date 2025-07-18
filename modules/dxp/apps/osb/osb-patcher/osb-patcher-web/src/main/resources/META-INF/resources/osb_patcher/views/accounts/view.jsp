<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherAccountsViewDisplayContext patcherAccountsViewDisplayContext = new PatcherAccountsViewDisplayContext(request, renderRequest, renderResponse);
%>

<liferay-ui:header
	title='<%= LanguageUtil.format(request, "view-x", patcherAccountsViewDisplayContext.getAccountEntryCode()) %>'
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new PatcherAccountsViewManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, patcherAccountsViewDisplayContext.getSearchContainer()) %>"
/>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-builds"
	searchContainer="<%= patcherAccountsViewDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherBuild"
		escapedModel="<%= true %>"
		keyProperty="patcherBuildId"
		modelVar="patcherBuild"
	>
		<liferay-ui:search-container-row-parameter
			name="className"
			value='<%= "patcher-build-type-" + PatcherBuildConstants.getTypeLabel(patcherBuild.getType()) %>'
		/>

		<portlet:renderURL var="viewPatcherBuildPatcherFixesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes_builds" />
			<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
		</portlet:renderURL>

		<%
		boolean hasPermissions = PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.FIXES, patcherBuild.getUserId());
		%>

		<liferay-ui:search-container-column-text
			cssClass="osb-patcher-search-container-column-text-icon"
		>
			<c:if test="<%= PatcherBuildUtil.isObsolete(patcherBuild.getPatcherBuildId()) %>">
				<clay:link
					aria-label='<%= LanguageUtil.get(request, "this-build-is-obsolete") %>'
					cssClass="lfr-portal-tooltip"
					href='<%= hasPermissions ? "javascript:void(0);" : StringPool.BLANK %>'
					icon="check-circle"
					onClick='<%= hasPermissions ? liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-fixes-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + viewPatcherBuildPatcherFixesURL + "');" : StringPool.BLANK %>'
					title='<%= LanguageUtil.get(request, "this-build-is-obsolete") %>'
				/>
			</c:if>

			<c:if test="<%= PatcherFixUtil.containsPatcherFixWorkaround(patcherBuild.getPatcherBuildId()) %>">
				<clay:link
					aria-label='<%= LanguageUtil.get(request, "this-build-contains-workaround-fixes") %>'
					cssClass="lfr-portal-tooltip"
					href='<%= hasPermissions ? "javascript:void(0);" : StringPool.BLANK %>'
					icon="warning"
					onClick='<%= hasPermissions ? liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-fixes-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + viewPatcherBuildPatcherFixesURL + "');" : StringPool.BLANK %>'
					title='<%= LanguageUtil.get(request, "this-build-contains-workaround-fixes") %>'
				/>
			</c:if>

			<c:if test="<%= PatcherFixUtil.containsPatcherFixComment(patcherBuild.getPatcherBuildId()) %>">
				<clay:link
					aria-label='<%= LanguageUtil.get(request, "this-build-contains-fixes-with-comments") %>'
					cssClass="lfr-portal-tooltip"
					href='<%= hasPermissions ? "javascript:void(0);" : StringPool.BLANK %>'
					icon="message"
					onClick='<%= hasPermissions ? liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-fixes-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + viewPatcherBuildPatcherFixesURL + "');" : StringPool.BLANK %>'
					title='<%= LanguageUtil.get(request, "this-build-contains-fixes-with-comments") %>'
				/>
			</c:if>
		</liferay-ui:search-container-column-text>

		<portlet:renderURL var="viewPatcherBuildURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds" />
			<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			href="<%= viewPatcherBuildURL %>"
			name="build-id"
			property="patcherBuildId"
		/>

		<liferay-ui:search-container-column-text
			name="type"
			value="<%= LanguageUtil.get(request, PatcherBuildConstants.getTypeLabel(patcherBuild.getType())) %>"
		/>

		<liferay-ui:search-container-column-text
			cssClass="nobr"
			href="<%= PatcherBuildUtil.getSupportTicketURL(patcherBuild.getSupportTicket()) %>"
			name="support-ticket"
			target="_blank"
			value="<%= patcherBuild.getSupportTicket() %>"
		/>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<%
		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherBuild.getPatcherProjectVersionId());
		%>

		<liferay-ui:search-container-column-text
			name="project-version"
			value="<%= patcherProjectVersion.getName() %>"
		/>

		<liferay-ui:search-container-column-text
			name="content"
		>
			<portlet:renderURL var="viewPatcherBuildContentURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
				<portlet:param name="mvcRenderCommandName" value="/patcher/view_project_versions_fixed_issues" />
				<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
				<portlet:param name="patcherProjectVersionId" value="<%= String.valueOf(patcherBuild.getPatcherProjectVersionId()) %>" />
			</portlet:renderURL>

			<%
			String label = StringPool.BLANK;

			int patcherFixPackNamesCount = PatcherFixPackUtil.getPatcherFixPackNamesCount(patcherBuild.getName());

			if (patcherFixPackNamesCount > 0) {
				label += patcherFixPackNamesCount + " " + LanguageUtil.get(request, "fix-packs") + " ";
			}

			int ticketsCount = PatcherUtil.getTicketsCount(patcherBuild.getName());

			if (ticketsCount > 0) {
				label += ticketsCount + " " + LanguageUtil.get(request, "tickets");
			}
			%>

			<clay:button
				cssClass="nobr"
				displayType="link"
				label="<%= label %>"
				onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.get(request, "content") + "', '" + viewPatcherBuildContentURL + "');" %>'
			/>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="patcher-status"
			value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherBuild.getStatus())) %>"
		/>

		<liferay-ui:search-container-column-text
			name="jenkins"
		>

			<%
			for (Map<String, String> jenkinsResult : JenkinsUtil.getJenkinsResults(patcherBuild)) {
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

		<portlet:renderURL var="editPatcherBuildCommentsFieldURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcRenderCommandName" value="/patcher/edit_comments_field_builds" />
			<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			name="engineer-comments"
		>
			<c:choose>
				<c:when test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherBuild.getUserId()) && (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<clay:button
						displayType="link"
						label="<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherBuild.getComments(), 75) %>"
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "edit-engineer-comments-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + editPatcherBuildCommentsFieldURL + "');" %>'
					/>
				</c:when>
				<c:otherwise>
					<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherBuild.getComments(), 75) %>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="qa-status"
			value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherBuild.getQaStatus())) %>"
		/>

		<portlet:renderURL var="editPatcherBuildQAFieldsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcRenderCommandName" value="/patcher/edit_qa_fields_builds" />
			<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			name="qa-comments"
		>
			<c:choose>
				<c:when test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.EDIT_QA_FIELDS, patcherBuild.getUserId()) && (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<clay:button
						displayType="link"
						label="<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherBuild.getQaComments(), 75) %>"
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "edit-qa-status-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + editPatcherBuildQAFieldsURL + "');" %>'
					/>
				</c:when>
				<c:otherwise>
					<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherBuild.getQaComments(), 75) %>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-column-text>

		<%
		String fileName = patcherBuild.getFileName();
		%>

		<liferay-ui:search-container-column-text
			cssClass="nobr"
			href='<%= fileName.contains("/liferay-dxp-") ? "https://releases-cdn.liferay.com/dxp/hotfix" : patcherConfiguration.patcherBuildDownloadURL() + "/" + fileName %>'
			name="hotfix"
			target="_blank"
			value="<%= PatcherBuildUtil.isCompleteReadyOrReleased(patcherBuild) ? PatcherBuildUtil.getLiferayHotfixFileName(fileName) : StringPool.BLANK %>"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<clay:dropdown-actions
				aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
				dropdownItems="<%= patcherAccountsViewDisplayContext.getDropdownItems(patcherBuild) %>"
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