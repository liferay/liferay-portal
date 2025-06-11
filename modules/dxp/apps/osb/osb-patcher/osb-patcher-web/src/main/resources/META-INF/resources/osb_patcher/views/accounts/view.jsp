<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherAccountsViewDisplayContext patcherAccountsViewDisplayContext = new PatcherAccountsViewDisplayContext(request, renderRequest, renderResponse);

long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");
%>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="accounts" />
	</liferay-util:include>
</c:if>

<portlet:renderURL var="viewPatcherAccountURL">
	<portlet:param name="mvcRenderCommandName" value="/patcher/view_accounts" />
	<portlet:param name="patcherBuildAccountEntryCode" value="<%= patcherAccountsViewDisplayContext.getPatcherBuildAccountEntryCode() %>" />
</portlet:renderURL>

<div class="layout">
	<div class="layout-content">
		<clay:col>
			<aui:select label="product-version" name="patcherProductVersionId" onChange='<%= liferayPortletResponse.getNamespace() + "productVersionOnChange(this.value);" %>' showEmptyOption="<%= true %>">

				<%
				for (PatcherProductVersion patcherProductVersion : PatcherProductVersionLocalServiceUtil.getPatcherProductVersions()) {
				%>

					<aui:option label="<%= patcherProductVersion.getName() %>" value="<%= patcherProductVersion.getPatcherProductVersionId() %>" />

				<%
				}
				%>

				<aui:option label="any" value="0" />
			</aui:select>
		</clay:col>
	</div>
</div>

<aui:button-row>
	<portlet:renderURL var="createPatcherBuildURL">
		<portlet:param name="mvcRenderCommandName" value="/patcher/create_builds" />
		<portlet:param name="patcherProductVersionId" value="<%= String.valueOf(patcherProductVersionId) %>" />
		<portlet:param name="redirect" value="<%= viewPatcherAccountURL %>" />
	</portlet:renderURL>

	<aui:button disabled='<%= !PatcherPermission.contains(permissionChecker, "builds", PatcherActionKeys.CREATE) %>' href="<%= createPatcherBuildURL %>" value="create-build" />
</aui:button-row>

<aui:form action="<%= viewPatcherAccountURL %>" method="get" name="fm">
	<liferay-ui:search-toggle
		buttonLabel="search"
		displayTerms="<%= new DisplayTerms(renderRequest) %>"
		id="toggle_id_patcher_build_search"
	>
		<div class="layout">
			<div class="layout-content">
				<clay:col>
					<aui:input label="build-id" name="<%= Field.ENTRY_CLASS_PK %>" size="30" type="text" />
				</clay:col>

				<clay:col>
					<aui:input label="content" name="patcherBuildName" size="30" title="search-builds" type="text" />
				</clay:col>

				<clay:col>
					<aui:input label="account-code" name="patcherBuildAccountEntryCode" size="30" title="search-accounts" type="text" />
				</clay:col>

				<clay:col>
					<aui:input label="support-ticket" name="supportTicket" size="30" title="search-support-tickets" type="text" />
				</clay:col>

				<clay:col>
					<aui:input checked="<%= true %>" name="hideOldBuildVersions" type="checkbox" />
				</clay:col>
			</div>
		</div>

		<div class="layout">
			<div class="layout-content">
				<clay:col>
					<aui:select label="patcher-status" name="statusFilter" showEmptyOption="<%= true %>">
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
					<aui:select label="type" name="typeFilter" showEmptyOption="<%= true %>">
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
			<liferay-ui:icon
				image='<%= PatcherBuildUtil.isObsolete(patcherBuild.getPatcherBuildId()) ? "../common/activate" : StringPool.BLANK %>'
				message="this-build-is-obsolete"
				onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-fixes-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + viewPatcherBuildPatcherFixesURL + "');" %>'
				url='<%= hasPermissions ? "javascript:void(0);" : StringPool.BLANK %>'
			/>

			<liferay-ui:icon
				image='<%= PatcherFixUtil.containsPatcherFixWorkaround(patcherBuild.getPatcherBuildId()) ? "../api/exception" : StringPool.BLANK %>'
				message="this-build-contains-workaround-fixes"
				onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-fixes-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + viewPatcherBuildPatcherFixesURL + "');" %>'
				url='<%= hasPermissions ? "javascript:void(0);" : StringPool.BLANK %>'
			/>

			<liferay-ui:icon
				image='<%= PatcherFixUtil.containsPatcherFixComment(patcherBuild.getPatcherBuildId()) ? "../common/message" : StringPool.BLANK %>'
				message="this-build-contains-fixes-with-comments"
				onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-fixes-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + viewPatcherBuildPatcherFixesURL + "');" %>'
				url='<%= hasPermissions ? "javascript:void(0);" : StringPool.BLANK %>'
			/>
		</liferay-ui:search-container-column-text>

		<portlet:renderURL var="viewPatcherBuildURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds" />
			<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
			<portlet:param name="redirect" value="<%= viewPatcherAccountURL %>" />
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
				<portlet:param name="mvcRenderCommandName" value="/patcher/view_content_builds" />
				<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
			</portlet:renderURL>

			<clay:button
				cssClass="nobr"
				displayType="link"
				label='<%= PatcherFixPackUtil.getPatcherFixPackNamesCount(patcherBuild.getName()) + " " + LanguageUtil.get(request, "fix-packs") + " - " + PatcherUtil.getTicketsCount(patcherBuild.getName()) + " " + LanguageUtil.get(request, "tickets") %>'
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
			target="_blank"
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
						<portlet:param name="redirect" value="<%= viewPatcherAccountURL %>" />
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
						<portlet:param name="redirect" value="<%= viewPatcherAccountURL %>" />
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

				<c:if test="<%= patcherBuild.getStatus() == WorkflowConstants.STATUS_BUILD_COMPLETE %>">

					<%
					String releaseConfirmMessageKey = "this-patch-has-not-passed-qa-testing-are-you-sure-this-patch-is-ready-for-release";

					if (PatcherBuildUtil.isTestingPassed(patcherBuild)) {
						releaseConfirmMessageKey = "are-you-sure-this-patch-is-ready-for-release";
					}
					%>

					<portlet:actionURL name="/patcher/ready_for_release_builds" var="releasePatcherBuildURL">
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="ready-for-release"
						method="get"
						onClick='<%= liferayPortletResponse.getNamespace() + "confirm('" + LanguageUtil.get(request, releaseConfirmMessageKey) + "', '" + releasePatcherBuildURL + "');" %>'
						url="javascript:void(0);"
					/>
				</c:if>

				<c:if test="<%= (patcherBuild.getStatus() == WorkflowConstants.STATUS_BUILD_COMPLETE) || (patcherBuild.getStatus() == WorkflowConstants.STATUS_BUILD_READY_TO_RELEASE) %>">

					<%
					String releaseConfirmMessageKey = "this-patch-has-not-passed-qa-testing-are-you-sure-you-want-to-release-this-patch-to-the-customer";

					if (PatcherBuildUtil.isTestingPassed(patcherBuild)) {
						releaseConfirmMessageKey = "are-you-sure-you-want-to-release-this-patch-to-the-customer";
					}
					%>

					<portlet:actionURL name="/patcher/release_manually_builds" var="releasePatcherBuildURL">
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="release-manually"
						method="get"
						onClick='<%= liferayPortletResponse.getNamespace() + "confirm('" + LanguageUtil.get(request, releaseConfirmMessageKey) + "', '" + releasePatcherBuildURL + "');" %>'
						url="javascript:void(0);"
					/>

					<portlet:actionURL name="/patcher/release_to_help_center_builds" var="releasePatcherBuildURL">
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="release-to-help-center"
						method="get"
						onClick='<%= liferayPortletResponse.getNamespace() + "confirm('" + LanguageUtil.get(request, releaseConfirmMessageKey) + "', '" + releasePatcherBuildURL + "');" %>'
						url="javascript:void(0);"
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
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-child-builds-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + viewChildPatcherBuildsURL + "');" %>'
						url="javascript:void(0);"
					/>
				</c:if>
			</liferay-ui:icon-menu>
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

	Liferay.provide(
		window,
		'<portlet:namespace />productVersionOnChange',
		function(productVersionId) {
			var namespace = '<portlet:namespace />';

			window.location.href = Liferay.Patcher.updateProductVersionId('<%= viewPatcherAccountURL %>', productVersionId, namespace);
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />confirm',
		function(message, url) {
			if (confirm(message)) {
				window.location.href = url;
			}
		}
	);
</aui:script>