<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherBuildsDisplayContext patcherBuildsDisplayContext = new PatcherBuildsDisplayContext(request, renderRequest, renderResponse);
%>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="qa-builds" />
</liferay-util:include>

<portlet:renderURL var="viewPatcherBuildsURL">
	<portlet:param name="mvcRenderCommandName" value="/patcher/index_builds" />
</portlet:renderURL>

<aui:form action="<%= viewPatcherBuildsURL %>" method="get" name="fm">
	<liferay-ui:search-toggle
		buttonLabel="search"
		displayTerms="<%= new DisplayTerms(renderRequest) %>"
		id="toggle_id_patcher_build_search"
	>
		<aui:input name="patcherProductVersionId" type="hidden" value='<%= ParamUtil.getLong(request, "patcherProductVersionId") %>' />

		<div class="layout">
			<div class="layout-content">
				<clay:col>
					<aui:input label="build-id" name="<%= Field.ENTRY_CLASS_PK %>" size="30" type="text" />
				</clay:col>

				<clay:col>
					<aui:input label="name" name="patcherBuildName" size="30" title="search-builds" type="text" />
				</clay:col>

				<clay:col>
					<aui:input label="account-code" name="patcherBuildAccountEntryCode" size="30" title="search-accounts" type="text" />
				</clay:col>

				<clay:col>
					<aui:input name="supportTicket" size="30" title="search-support-tickets" type="text" />
				</clay:col>

				<clay:col>
					<aui:input checked="<%= true %>" name="hideOldBuildVersions" type="checkbox" />
				</clay:col>
			</div>
		</div>

		<div class="layout">
			<div class="layout-content">
				<clay:col>
					<aui:select label="status" name="statusFilter" showEmptyOption="<%= true %>">
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_MERGING %>" value="<%= WorkflowConstants.STATUS_BUILD_MERGING %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_COMPILING %>" value="<%= WorkflowConstants.STATUS_BUILD_COMPILING %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_CONFLICT %>" value="<%= WorkflowConstants.STATUS_BUILD_CONFLICT %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_COMPLETE %>" value="<%= WorkflowConstants.STATUS_BUILD_COMPLETE %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_READY_TO_RELEASE %>" value="<%= WorkflowConstants.STATUS_BUILD_READY_TO_RELEASE %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_RELEASED %>" value="<%= WorkflowConstants.STATUS_BUILD_RELEASED %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_FAILED %>" value="<%= WorkflowConstants.STATUS_BUILD_FAILED %>" />
					</aui:select>
				</clay:col>

				<clay:col>
					<aui:select label="qa-status" name="qaStatusFilter" showEmptyOption="<%= true %>">
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_NEEDED %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_STARTED %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_STARTED %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_PASSED %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_STARTED %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_FAILED_MANUALLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_PASSED_MANUALLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_PENDING %>" value="<%= WorkflowConstants.STATUS_PENDING %>" />
						<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_PENDING_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_PENDING_SMOKE_ONLY %>" />
					</aui:select>
				</clay:col>

				<clay:col>
					<aui:select label="type" name="typeFilter" showEmptyOption="<%= false %>">
						<aui:option label="<%= PatcherBuildConstants.LABEL_OFFICIAL %>" value="<%= PatcherBuildConstants.TYPE_OFFICIAL %>" />
						<aui:option label="<%= PatcherBuildConstants.LABEL_DEBUG %>" value="<%= PatcherBuildConstants.TYPE_DEBUG %>" />
						<aui:option label="<%= PatcherBuildConstants.LABEL_IGNORE %>" value="<%= PatcherBuildConstants.TYPE_IGNORE %>" />
						<aui:option label="<%= PatcherBuildConstants.LABEL_FIX_PACK %>" value="<%= PatcherBuildConstants.TYPE_FIX_PACK %>" />
					</aui:select>
				</clay:col>

				<clay:col>
					<aui:select label="project-version" name="patcherProjectVersionIdFilter" showEmptyOption="<%= true %>">

						<%
						for (PatcherProjectVersion patcherProjectVersion : PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions()) {
						%>

							<aui:option label="<%= patcherProjectVersion.getName() %>" value="<%= patcherProjectVersion.getPatcherProjectVersionId() %>" />

						<%
						}
						%>

					</aui:select>
				</clay:col>
			</div>
		</div>
	</liferay-ui:search-toggle>
</aui:form>

<liferay-ui:search-container
	searchContainer="<%= patcherBuildsDisplayContext.getSearchContainer() %>"
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
			<liferay-ui:icon
				image='<%= PatcherBuildUtil.isObsolete(patcherBuild.getPatcherBuildId()) ? "../common/activate" : StringPool.BLANK %>'
				message="this-build-is-obsolete"
				onClick='<%= hasPermissions ? liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-fixes-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + viewPatcherBuildPatcherFixesURL + "');" : StringPool.BLANK %>'
				url='<%= hasPermissions ? "javascript:void(0);" : StringPool.BLANK %>'
			/>

			<liferay-ui:icon
				image='<%= PatcherFixUtil.containsPatcherFixWorkaround(patcherBuild.getPatcherBuildId()) ? "../api/exception" : StringPool.BLANK %>'
				message="this-build-contains-workaround-fixes"
				onClick='<%= hasPermissions ? liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-fixes-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + viewPatcherBuildPatcherFixesURL + "');" : StringPool.BLANK %>'
				url='<%= hasPermissions ? "javascript:void(0);" : StringPool.BLANK %>'
			/>

			<liferay-ui:icon
				image='<%= PatcherFixUtil.containsPatcherFixComment(patcherBuild.getPatcherBuildId()) ? "../common/message" : StringPool.BLANK %>'
				message="this-build-contains-fixes-with-comments"
				onClick='<%= hasPermissions ? liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-fixes-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + viewPatcherBuildPatcherFixesURL + "');" : StringPool.BLANK %>'
				url='<%= hasPermissions ? "javascript:void(0);" : StringPool.BLANK %>'
			/>
		</liferay-ui:search-container-column-text>

		<portlet:renderURL var="viewPatcherBuildURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds" />
			<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
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
			name="account-code"
			value="<%= PatcherBuildUtil.getPatcherAccountEntryCode(patcherBuild.getPatcherAccountId()) %>"
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
			name="product-version"
			value="<%= PatcherProductVersionUtil.fetchPatcherProductVersionName(patcherProjectVersion.getPatcherProductVersionId()) %>"
		/>

		<liferay-ui:search-container-column-text
			name="project-version"
			value="<%= patcherProjectVersion.getName() %>"
		/>

		<liferay-ui:search-container-column-text
			name="content"
		>
			<portlet:renderURL var="viewPatcherBuildContentURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
				<portlet:param name="mvcRenderCommandName" value="/patcher/view_content_builds" />
				<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
			</portlet:renderURL>

			<clay:button
				cssClass="nobr"
				displayType="link"
				label='<%= PatcherFixPackUtil.getPatcherFixPackNamesCount(patcherBuild.getName()) + " " + LanguageUtil.get(request, "fix-packs") + " " + PatcherUtil.getTicketsCount(patcherBuild.getName()) + " " + LanguageUtil.get(request, "tickets") %>'
				onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + LanguageUtil.get(request, "content") + "', '" + viewPatcherBuildContentURL + "');" %>'
			/>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="status-date"
		>
			<fmt:formatDate
				value="<%= patcherBuild.getStatusDate() %>"
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
					target="_blank"
					title='<%= jenkinsResult.get("jobName") %>'
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
			value="<%= PatcherBuildUtil.isCompleteReadyOrReleased(patcherBuild) ? PatcherBuildUtil.getLiferayHotfixFileName(fileName) : StringPool.BLANK %>"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu
				cssClass="osb-patcher-icon-menu"
			>
				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.EDIT, patcherBuild.getUserId()) && PatcherBuildUtil.isLatestPatcherBuild(patcherBuild) && (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<portlet:renderURL var="editPatcherBuildURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/edit_builds" />
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="<%= editPatcherBuildURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.EDIT, patcherBuild.getUserId()) && PatcherBuildUtil.isLatestPatcherBuild(patcherBuild) && (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<portlet:renderURL var="createPatcherBuildTemplateURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/create_builds" />
						<portlet:param name="templatePatcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						message="use-as-build-template"
						method="get"
						url="<%= createPatcherBuildTemplateURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherBuild.getUserId()) && (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<liferay-ui:icon
						image="edit"
						message="edit-engineer-comments"
						method="get"
						url="<%= editPatcherBuildCommentsFieldURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.EDIT_QA_FIELDS, patcherBuild.getUserId()) && (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<liferay-ui:icon
						image="edit"
						message="edit-qa-status"
						method="get"
						url="<%= editPatcherBuildQAFieldsURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.SEND_REQUEST, patcherBuild.getUserId()) && JenkinsUtil.isValidJenkinsSetup() && JenkinsUtil.isValidSendDistJenkinsRequest(patcherBuild) && (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<portlet:actionURL name="/patcher/build_builds" var="buildPatcherBuildURL">
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="build"
						method="get"
						url="<%= buildPatcherBuildURL %>"
					/>
				</c:if>

				<c:if test="<%= patcherBuild.getStatus() == WorkflowConstants.STATUS_BUILD_COMPLETE %>">
					<portlet:actionURL name="/patcher/test_builds" var="testPatcherBuildURL">
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="test"
						method="get"
						url="<%= testPatcherBuildURL %>"
					/>

					<portlet:actionURL name="/patcher/smoke_test_builds" var="smokeTestPatcherBuildURL">
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="smoke-test"
						method="get"
						url="<%= smokeTestPatcherBuildURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.FIXES, patcherBuild.getUserId()) && !PatcherBuildRelUtil.hasChildPatcherBuilds(patcherBuild) %>">
					<liferay-ui:icon
						image="view"
						message="view-fixes"
						method="get"
						url="<%= viewPatcherBuildPatcherFixesURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.CHILD_BUILDS, patcherBuild.getUserId()) && PatcherBuildRelUtil.hasChildPatcherBuilds(patcherBuild) %>">
					<portlet:renderURL var="viewChildPatcherBuildsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/patcher/view_child_builds_builds" />
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="view"
						message="view-child-builds"
						method="get"
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-child-builds-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + viewChildPatcherBuildsURL + "')" %>'
						url="javascript:void(0);"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>

<aui:script>
	function <portlet:namespace />handleClick(title, url) {
		Liferay.Util.openModal({
			title: title,
			url: url,
		});
	}
</aui:script>