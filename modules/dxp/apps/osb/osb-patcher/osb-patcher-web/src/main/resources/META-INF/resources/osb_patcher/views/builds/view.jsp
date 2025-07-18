<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherViewBuildsDisplayContext patcherViewBuildsDisplayContext = new PatcherViewBuildsDisplayContext(request, renderRequest, renderResponse);

PatcherBuild patcherBuild = patcherViewBuildsDisplayContext.getPatcherBuild();

PatcherBuild latestPatcherBuild = null;

if (patcherConfiguration.patcherScanningEnabled() && !patcherBuild.getLatestSupportTicketBuild()) {
	latestPatcherBuild = PatcherBuildUtil.fetchPatcherBuildByLatestSupportTicketBuild(patcherBuild.getSupportTicket());
}
else if (!patcherBuild.getLatestKeyBuild()) {
	latestPatcherBuild = PatcherBuildUtil.fetchPatcherBuildByLatestKeyBuild(patcherBuild.getKey());
}
%>

<liferay-ui:header
	title="view-build"
/>

<aui:model-context bean="<%= patcherBuild %>" model="<%= PatcherBuild.class %>" />

<clay:container-fluid
	cssClass="container-form-lg container-no-gutters"
>
	<clay:sheet
		size="full"
	>
		<h2 class="sheet-title">
			<liferay-ui:message key="view-build" />
		</h2>

		<c:if test="<%= (latestPatcherBuild != null) && !PatcherBuildUtil.isLatestPatcherBuild(patcherBuild) %>">
			<portlet:renderURL var="viewLatestPatcherBuildURL">
				<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds" />
				<portlet:param name="patcherBuildId" value="<%= String.valueOf(latestPatcherBuild.getPatcherBuildId()) %>" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
			</portlet:renderURL>

			<clay:alert
				displayType="warning"
			>
				<clay:link
					href="<%= viewLatestPatcherBuildURL %>"
					label="this-is-not-the-latest-build-version-view-the-latest-build-here"
				/>
			</clay:alert>
		</c:if>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="create-date" />
			</p>

			<p class="text-secondary">
				<%= dateTimeFormat.format(patcherBuild.getCreateDate()) %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="modified-date" />
			</p>

			<p class="text-secondary">
				<%= dateTimeFormat.format(patcherBuild.getModifiedDate()) %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="status-date" />
			</p>

			<p class="text-secondary">
				<%= dateTimeFormat.format(patcherBuild.getStatusDate()) %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="created-by" />
			</p>

			<p class="text-secondary">
				<%= patcherBuild.getUserName() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="status-updated-by" />
			</p>

			<p class="text-secondary">
				<%= patcherBuild.getStatusByUserName() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="build-id" />
			</p>

			<p class="text-secondary">
				<%= patcherBuild.getPatcherBuildId() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="version" />
			</p>

			<p class="text-secondary">
				<%= patcherBuild.getKeyVersion() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="status" />
			</p>

			<liferay-portal-workflow:status
				showStatusLabel="<%= false %>"
				status="<%= patcherBuild.getStatus() %>"
				statusMessage="<%= WorkflowConstants.getStatusLabel(patcherBuild.getStatus()) %>"
			/>

			<c:if test="<%= patcherBuild.getStatus() == WorkflowConstants.STATUS_BUILD_FAILED %>">
				<clay:link
					href="<%= patcherConfiguration.troubleshootingURL() %>"
					label="troubleshooting-guide"
					target="_blank"
				/>
			</c:if>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="qa-status" />
			</p>

			<liferay-portal-workflow:status
				showStatusLabel="<%= false %>"
				status="<%= patcherBuild.getQaStatus() %>"
				statusMessage="<%= WorkflowConstants.getStatusLabel(patcherBuild.getQaStatus()) %>"
			/>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="qa-comments" />
			</p>

			<p class="text-secondary">
				<%= patcherBuild.getQaComments() %>
			</p>
		</div>

		<%
		PatcherProductVersion patcherProductVersion = PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(patcherBuild.getPatcherProductVersionId());
		%>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="product-version" />
			</p>

			<p class="text-secondary">
				<%= patcherProductVersion.getName() %>
			</p>
		</div>

		<%
		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherBuild.getPatcherProjectVersionId());
		%>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="project-version" />
			</p>

			<p class="text-secondary">
				<%= patcherProjectVersion.getName() %>
			</p>
		</div>

		<%
		PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(patcherBuild.getPatcherFixId());
		%>

		<c:if test="<%= patcherFix != null %>">
			<div class="c-mb-3">
				<p class="c-mb-1 font-weight-semi-bold text-3">
					<liferay-ui:message key="git-hash" />
				</p>

				<p class="text-secondary">
					<a href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()) %>" target="_blank"><%= patcherFix.getGitHash() %></a>
				</p>
			</div>
		</c:if>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="jenkins" />
			</p>

			<p class="text-secondary">

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

			</p>
		</div>

		<%
		String fileName = patcherBuild.getFileName();
		%>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="hotfix" />
			</p>

			<p class="text-secondary">
				<c:choose>
					<c:when test='<%= fileName.contains("/liferay-dxp-") %>'>
						<a href="https://releases-cdn.liferay.com/dxp/hotfix/<%= fileName %>" target="_blank">https://releases-cdn.liferay.com/dxp/hotfix/<%= fileName %></a><br />

						<pre>patching-tool install hotfix-<%= PatcherBuildUtil.getHotfixIdByFileName(fileName) %></pre>
						<pre>patching-tool.sh install hotfix-<%= PatcherBuildUtil.getHotfixIdByFileName(fileName) %></pre>
					</c:when>
					<c:otherwise>
						<a href="<%= patcherConfiguration.patcherBuildDownloadURL() %>/<%= fileName %>" target="_blank"><%= Validator.isNotNull(fileName) ? LanguageUtil.get(request, "download") : StringPool.BLANK %></a>
					</c:otherwise>
				</c:choose>
			</p>
		</div>

		<c:if test="<%= Validator.isNotNull(patcherBuild.getSourceName()) %>">
			<div class="c-mb-3">
				<p class="c-mb-1 font-weight-semi-bold text-3">
					<liferay-ui:message key="source-zip" />
				</p>

				<p class="text-secondary">
					<a href="<%= patcherConfiguration.patcherBuildDownloadURL() %>/<%= patcherBuild.getSourceName() %>" target="_blank"><liferay-ui:message key="download" /></a>
				</p>
			</div>
		</c:if>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="tickets-list" />
			</p>

			<p class="text-secondary">

				<%
				List<String> tickets = ListUtil.sort(StringUtil.split(patcherBuild.getName()));

				for (String ticket : tickets) {
				%>

					<clay:link
						href='<%= "https://liferay.atlassian.net/browse/" + ticket %>'
						label="<%= ticket %>"
						target="_blank"
					/>

					<%= StringPool.NBSP %>

				<%
				}
				%>

			</p>
		</div>

		<c:if test="<%= (patcherBuild.getPatcherProductVersionId() != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) && !patcherBuild.isChildBuild() && !StringUtil.equalsIgnoreCase(patcherBuild.getName(), patcherBuild.getInitialName()) %>">
			<div class="c-mb-3">
				<p class="c-mb-1 font-weight-semi-bold text-3">
					<liferay-ui:message key="original-tickets-list" />
				</p>

				<p class="text-secondary">
					<%= patcherBuild.getInitialName() %>
				</p>

				<liferay-ui:icon
					image="../api/exception"
					message=""
				/>

				<clay:link
					href="<%= patcherConfiguration.infoModifyTicketsListURL() %>"
					label="click-here-to-find-out-why-the-ticket-list-changed"
					target="_blank"
				/>
			</div>
		</c:if>

		<%
		PatcherAccount patcherAccount = PatcherAccountLocalServiceUtil.getPatcherAccount(patcherBuild.getPatcherAccountId());
		%>

		<portlet:renderURL var="viewPatcherAccountPatcherProductVersionURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_accounts" />
			<portlet:param name="accountEntryCode" value="<%= patcherAccount.getAccountEntryCode() %>" />
			<portlet:param name="patcherProductVersionId" value="<%= String.valueOf(patcherBuild.getPatcherProductVersionId()) %>" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
		</portlet:renderURL>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="account-code" />
			</p>

			<p class="text-secondary">
				<clay:link
					href="<%= viewPatcherAccountPatcherProductVersionURL %>"
					label="<%= patcherAccount.getAccountEntryCode() %>"
				/>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="support-ticket" />
			</p>

			<p class="text-secondary">
				<%= patcherBuild.getSupportTicket() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="type" />
			</p>

			<p class="text-secondary">
				<%= PatcherBuildConstants.getTypeLabel(patcherBuild.getType()) %>
			</p>
		</div>

		<aui:input disabled="<%= true %>" name="mergeOnly" type="checkbox" value="<%= PatcherBuildUtil.isMergeOnly(patcherBuild) %>" />

		<aui:button-row>
			<c:if test="<%= PatcherBuildUtil.isLatestPatcherBuild(patcherBuild) && (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) && !PatcherBuildRelUtil.hasParentPatcherBuilds(patcherBuild) %>">
				<portlet:renderURL var="editPatcherBuildURL">
					<portlet:param name="mvcRenderCommandName" value="/patcher/edit_builds" />
					<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
				</portlet:renderURL>

				<aui:button href="<%= editPatcherBuildURL %>" primary="<%= true %>" value="edit" />
			</c:if>

			<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) && !PatcherBuildRelUtil.hasChildPatcherBuilds(patcherBuild) %>">
				<portlet:renderURL var="viewPatcherBuildPatcherFixesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
					<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes_builds" />
					<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
				</portlet:renderURL>

				<clay:button
					displayType="secondary"
					label='<%= LanguageUtil.get(request, "view-fixes") %>'
					onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-fixes-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + viewPatcherBuildPatcherFixesURL + "');" %>'
				/>
			</c:if>

			<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.CHILD_BUILDS, patcherBuild.getUserId()) && PatcherBuildRelUtil.hasChildPatcherBuilds(patcherBuild) %>">
				<portlet:renderURL var="viewChildPatcherBuildsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
					<portlet:param name="mvcRenderCommandName" value="/patcher/view_child_builds_builds" />
					<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
				</portlet:renderURL>

				<clay:button
					displayType="secondary"
					label='<%= LanguageUtil.get(request, "view-child-builds") %>'
					onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-child-builds-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + viewChildPatcherBuildsURL + "');" %>'
				/>
			</c:if>

			<liferay-ui:icon-menu
				cssClass="osb-patcher-icon-menu"
			>
				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.EDIT, patcherBuild.getUserId()) && PatcherBuildUtil.isLatestPatcherBuild(patcherBuild) && (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<portlet:renderURL var="createPatcherBuildTemplateURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/add_builds" />
						<portlet:param name="templatePatcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						message="use-as-build-template"
						method="get"
						url="<%= createPatcherBuildTemplateURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherBuild.getUserId()) && (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<portlet:renderURL var="editPatcherBuildCommentsFieldURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/patcher/edit_comments_field_builds" />
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						message="edit-engineer-comments"
						method="get"
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "edit-engineer-comments-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + editPatcherBuildCommentsFieldURL + "');" %>'
						url="javascript:void(0);"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.EDIT_QA_FIELDS, patcherBuild.getUserId()) && (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<portlet:renderURL var="editPatcherBuildQAFieldsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/patcher/edit_qa_fields_builds" />
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						message="edit-qa-status"
						method="get"
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "edit-qa-status-for-build-id-x", patcherBuild.getPatcherBuildId()) + "', '" + editPatcherBuildQAFieldsURL + "');" %>'
						url="javascript:void(0);"
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
						<portlet:param name="status" value="<%= String.valueOf(WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="test"
						method="get"
						url="<%= testPatcherBuildURL %>"
					/>

					<portlet:actionURL name="/patcher/test_builds" var="smokeTestPatcherBuildURL">
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
						<portlet:param name="status" value="<%= String.valueOf(WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY) %>" />
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
					String releaseConfirmMessageKey = "this-patch-has-not-passed-qa-testing-are-you-sure-you-want-to-release-this-patch-to-the-customer";

					if (PatcherBuildUtil.isTestingPassed(patcherBuild)) {
						releaseConfirmMessageKey = "are-you-sure-you-want-to-release-this-patch-to-the-customer";
					}
					%>

					<portlet:actionURL name="/patcher/release_builds" var="releasePatcherBuildURL">
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
						<portlet:param name="status" value="<%= String.valueOf(WorkflowConstants.STATUS_BUILD_READY_TO_RELEASE) %>" />
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

					<portlet:actionURL name="/patcher/release_builds" var="releasePatcherBuildURL">
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
						<portlet:param name="status" value="<%= String.valueOf(WorkflowConstants.STATUS_BUILD_RELEASED) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="release-manually"
						method="get"
						onClick='<%= liferayPortletResponse.getNamespace() + "confirm('" + LanguageUtil.get(request, releaseConfirmMessageKey) + "', '" + releasePatcherBuildURL + "');" %>'
						url="javascript:void(0);"
					/>

					<portlet:actionURL name="/patcher/release_builds" var="releasePatcherBuildURL">
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuild.getPatcherBuildId()) %>" />
						<portlet:param name="status" value="<%= String.valueOf(WorkflowConstants.STATUS_BUILD_RELEASED) %>" />
						<portlet:param name="releaseToHelpCenter" value="<%= Boolean.TRUE.toString() %>" />
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
			</liferay-ui:icon-menu>
		</aui:button-row>
	</clay:sheet>
</clay:container-fluid>

<%
SearchContainer<PatcherBuild> patcherBuildSearchContainer = patcherViewBuildsDisplayContext.getSearchContainer();
%>

<c:if test="<%= patcherBuildSearchContainer.getTotal() > 1 %>">
	<clay:container-fluid
		cssClass="container-no-gutters"
	>
		<clay:sheet
			size="full"
		>
			<h2 class="sheet-title">
				<liferay-ui:message key="build-versions" />
			</h2>

			<liferay-ui:search-container
				searchContainer="<%= patcherBuildSearchContainer %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.osb.patcher.model.PatcherBuild"
					escapedModel="<%= true %>"
					keyProperty="patcherBuildId"
					modelVar="patcherBuildKeyVersion"
				>
					<c:if test="<%= patcherBuild.getPatcherBuildId() == patcherBuildKeyVersion.getPatcherBuildId() %>">
						<liferay-ui:search-container-row-parameter
							name="className"
							value="selected"
						/>
					</c:if>

					<liferay-ui:search-container-column-user
						name="created-by"
						showDetails="<%= true %>"
						userId="<%= patcherBuildKeyVersion.getUserId() %>"
					/>

					<portlet:renderURL var="viewPatcherBuildKeyVersionURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds" />
						<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherBuildKeyVersion.getPatcherBuildId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:renderURL>

					<liferay-ui:search-container-column-text
						href="<%= (patcherBuild.getPatcherBuildId() != patcherBuildKeyVersion.getPatcherBuildId()) ? viewPatcherBuildKeyVersionURL : null %>"
						name="build-id"
						property="patcherBuildId"
					/>

					<liferay-ui:search-container-column-text
						name="version"
						property="keyVersion"
					/>

					<liferay-ui:search-container-column-date
						name="modified-date"
						value="<%= patcherBuildKeyVersion.getModifiedDate() %>"
					/>

					<liferay-ui:search-container-column-text
						name="status"
						value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherBuildKeyVersion.getStatus())) %>"
					/>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
					paginate="<%= false %>"
				/>
			</liferay-ui:search-container>
		</clay:sheet>
	</clay:container-fluid>
</c:if>

<aui:script>
	function <portlet:namespace />handleClick(title, url) {
		Liferay.Util.openModal({
			title: title,
			url: url,
		});
	}

	Liferay.provide(
		window,
		'<portlet:namespace />confirm',
		function (message, url) {
			if (confirm(message)) {
				window.location.href = url;
			}
		}
	);
</aui:script>