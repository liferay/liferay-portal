<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherFixBuildsDisplayContext patcherFixBuildsDisplayContext = new PatcherFixBuildsDisplayContext(request, patcherConfiguration, renderRequest, renderResponse);

List<PatcherBuild> patcherBuilds = PatcherBuildLocalServiceUtil.getPatcherFixPatcherBuilds(patcherFixBuildsDisplayContext.getPatcherFixId());
%>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-builds"
	total="<%= patcherBuilds.size() %>"
>
	<liferay-ui:search-container-results
		results="<%= patcherBuilds %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherBuild"
		escapedModel="<%= true %>"
		keyProperty="patcherBuildId"
		modelVar="patcherBuild"
	>
		<liferay-ui:search-container-column-text>
			<c:if test="<%= PatcherBuildUtil.isObsolete(patcherBuild.getPatcherBuildId()) %>">
				<c:choose>
					<c:when test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.FIXES, patcherBuild.getUserId()) %>">
						<portlet:renderURL var="viewPatcherBuildPatcherFixesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
							<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes_builds" />
							<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
						</portlet:renderURL>

						<clay:link
							aria-label='<%= LanguageUtil.get(request, "this-build-is-obsolete") %>'
							cssClass="lfr-portal-tooltip"
							href="<%= viewPatcherBuildPatcherFixesURL %>"
							icon="check-circle"
							title='<%= LanguageUtil.get(request, "this-build-is-obsolete") %>'
						/>
					</c:when>
					<c:otherwise>
						<clay:icon
							cssClass="lfr-portal-tooltip"
							symbol="check-circle"
							title='<%= LanguageUtil.get(request, "this-build-is-obsolete") %>'
						/>
					</c:otherwise>
				</c:choose>
			</c:if>

			<c:if test="<%= PatcherFixUtil.containsPatcherFixWorkaround(patcherBuild.getPatcherBuildId()) %>">
				<clay:icon
					cssClass="lfr-portal-tooltip"
					symbol="warning"
					title='<%= LanguageUtil.get(request, "this-build-contains-workaround-fixes") %>'
				/>
			</c:if>
		</liferay-ui:search-container-column-text>

		<portlet:renderURL var="viewPatcherBuildURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds" />
			<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			name="build-id"
		>
			<clay:link
				href="<%= viewPatcherBuildURL %>"
				icon="warning"
				title="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>"
			/>
		</liferay-ui:search-container-column-text>

		<%
		String fileName = patcherBuild.getFileName();

		String hotfixURL = "https://releases-cdn.liferay.com/dxp/hotfix/" + fileName;

		if (!fileName.contains("/liferay-dxp-")) {
			hotfixURL = patcherConfiguration.patcherBuildDownloadURL() + "/" + fileName;
		}
		%>

		<liferay-ui:search-container-column-text
			cssClass="nobr"
			href="<%= hotfixURL %>"
			name="hotfix"
			target="_blank"
			value="<%= PatcherBuildUtil.isCompleteReadyOrReleased(patcherBuild) ? PatcherBuildUtil.getLiferayHotfixFileName(patcherBuild.getFileName()) : StringPool.BLANK %>"
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
			href="<%= PatcherBuildUtil.getSupportTicketURL(patcherBuild.getSupportTicket()) %>"
			name="support-ticket"
			target="_blank"
			value="<%= patcherBuild.getSupportTicket() %>"
		/>

		<%
		PatcherFix curPatcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(patcherBuild.getPatcherFixId());
		%>

		<liferay-ui:search-container-column-text
			href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherBuild.getPatcherFixId()) %>"
			name="git-hash"
			target="_blank"
			value="<%= com.liferay.portal.kernel.util.StringUtil.shorten(curPatcherFix.getGitHash(), 10) %>"
		/>

		<liferay-ui:search-container-column-text
			name="status"
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
					target="_blank"
					title='<%= jenkinsResult.get("jobName") %>'
				/>

			<%
			}
			%>

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
			<clay:dropdown-actions
				aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
				dropdownItems="<%= patcherFixBuildsDisplayContext.getDropdownItems(patcherBuild) %>"
				propsTransformer="{PatcherDropdownDefaultPropsTransformer} from osb-patcher-web"
			/>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
		paginate="<%= false %>"
	/>
</liferay-ui:search-container>