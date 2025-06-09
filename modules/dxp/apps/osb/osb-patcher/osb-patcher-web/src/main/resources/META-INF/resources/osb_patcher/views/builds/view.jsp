<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="builds" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="view-build" />
	<liferay-util:param name="mvcRenderCommandName" value="/patcher/index_builds" />
</liferay-util:include>

<aui:model-context bean="<%= patcherBuild %>" model="<%= PatcherBuild.class %>" />

<portlet:renderURL var="viewPatcherBuildURL">
	<portlet:param name="controller" value="builds" />
	<portlet:param name="action" value="view" />
	<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />

	<c:if test="<%= not empty redirect %>">
		<portlet:param name="redirect" value="<%= redirect %>" />
	</c:if>
</portlet:renderURL>

<c:if test="<%= !PatcherBuildUtil.isLatestPatcherBuild(patcherBuild) %>">
	<liferay-ui:message key="this-is-not-the-latest-build-version-view-the-latest-build-here" />

	<portlet:renderURL var="viewLatestPatcherBuildURL">
		<portlet:param name="controller" value="builds" />
		<portlet:param name="action" value="view" />
		<portlet:param name="id" value="<%= latestPatcherBuild.patcherBuildId %>" />
		<portlet:param name="redirect" value="<%= viewPatcherBuildURL %>" />
	</portlet:renderURL>

	<a href="<%= viewLatestPatcherBuildURL %>">
		<%= latestPatcherBuild.patcherBuildId %>
	</a>
</c:if>

<aui:field-wrapper name="createDate">
	<fmt:formatDate
		type="both"
		value="<%= patcherBuild.createDate %>"
	/>
</aui:field-wrapper>

<aui:field-wrapper name="modifiedDate">
	<fmt:formatDate
		type="both"
		value="<%= patcherBuild.modifiedDate %>"
	/>
</aui:field-wrapper>

<aui:field-wrapper name="statusDate">
	<fmt:formatDate
		type="both"
		value="<%= patcherBuild.statusDate %>"
	/>
</aui:field-wrapper>

<aui:field-wrapper name="createdBy">
	<%= patcherBuild.userName %>
</aui:field-wrapper>

<aui:field-wrapper name="statusUpdatedBy">
	<%= patcherBuild.statusByUserName %>
</aui:field-wrapper>

<aui:field-wrapper name="buildId">
	<%= patcherBuild.patcherBuildId %>
</aui:field-wrapper>

<aui:field-wrapper name="version">
	<%= patcherBuild.keyVersion %>
</aui:field-wrapper>

<aui:field-wrapper name="status">
	<%= patcherBuildStatus %>

	<c:if test="<%= patcherBuild.status == WorkflowConstants.STATUS_BUILD_FAILED %>">
		<clay:link
			href="<%= patcherConfiguration.troubleshootingURL() %>"
			target="_blank"
			title="troubleshooting-guide"
		/>
	</c:if>
</aui:field-wrapper>

<aui:field-wrapper name="qa-status">
	<liferay-ui:message key="<%= WorkflowConstants.getStatusLabel(patcherBuild.getQaStatus()) %>" />
</aui:field-wrapper>

<aui:field-wrapper name="qa-comments">
	<%= patcherBuild.qaComments %>
</aui:field-wrapper>

<aui:select disabled="<%= true %>" label="product-version" name="patcherProductVersionId" showEmptyOption="<%= true %>">
	<c:forEach items="<%= patcherProductVersions %>" var="patcherProductVersion">
		<aui:option label="<%= patcherProductVersion.getName() %>" value="<%= patcherProductVersion.getPatcherProductVersionId() %>" />
	</c:forEach>
</aui:select>

<aui:select disabled="<%= true %>" label="project-version" name="patcherProjectVersionId" showEmptyOption="<%= false %>">
	<c:forEach items="<%= patcherProjectVersions %>" var="patcherProjectVersion">
		<aui:option label="<%= patcherProjectVersion.name %>" value="<%= patcherProjectVersion.patcherProjectVersionId %>" />
	</c:forEach>
</aui:select>

<aui:field-wrapper name="git-hash">
	<a href="<%= gitHubURL %>" target="_blank"><%= gitHash %></a>
</aui:field-wrapper>

<aui:field-wrapper name="jenkins">
	<c:forEach items="<%= jenkinsResults %>" var="jenkinsResult">
		<clay:link
			cssClass="nobr"
			href="<%= jenkinsResult.statusURL %>"
			target="_blank"
			title="<%= jenkinsResult.jobName %>"
		/>
	</c:forEach>
</aui:field-wrapper>

<c:set value='<%= LanguageUtil.get(request, "download") %>' var="downloadURLLabel" />

<aui:field-wrapper name="hotfix">
	<c:choose>
		<c:when test='<%= patcherBuild.fileName.contains("/liferay-dxp-") %>'>
			<a href="https://releases-cdn.liferay.com/dxp/hotfix/<%= patcherBuild.fileName %>" target="_blank">https://releases-cdn.liferay.com/dxp/hotfix/<%= patcherBuild.fileName %></a><br />

			<pre>patching-tool install hotfix-<%= PatcherBuildUtil.getHotfixIdByFileName(patcherBuild.fileName) %></pre>
			<pre>patching-tool.sh install hotfix-<%= PatcherBuildUtil.getHotfixIdByFileName(patcherBuild.fileName) %></pre>
		</c:when>
		<c:otherwise>
			<a href="<%= patcherConfiguration.patcherBuildDownloadURL() %>/<%= patcherBuild.fileName %>" target="_blank"><%= not empty patcherBuild.fileName ? downloadURLLabel : StringPool.BLANK %></a>
		</c:otherwise>
	</c:choose>
</aui:field-wrapper>

<c:if test="<%= not empty patcherBuild.sourceName %>">
	<aui:field-wrapper name="sourceZip">
		<a href="<%= patcherConfiguration.patcherBuildDownloadURL() %>/<%= patcherBuild.sourceName %>" target="_blank"><%= downloadURLLabel %></a>
	</aui:field-wrapper>
</c:if>

<aui:input inputCssClass="osb-patcher-input-wide osb-patcher-read-only" label="tickets-list" name="patcherBuildName" readonly="<%= true %>" type="textarea" value="<%= patcherBuild.name %>" />

<c:if test="<%= (patcherBuild.patcherProductVersionId != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) && !patcherBuild.childBuild && !StringUtil.equalsIgnoreCase(patcherBuild.name, patcherBuild.originalName) %>">
	<aui:field-wrapper>
		<aui:input inputCssClass="osb-patcher-input-wide osb-patcher-read-only" label="original-tickets-list" name="patcherBuildOriginalName" readonly="<%= true %>" type="textarea" value="<%= patcherBuild.originalName %>" />

		<aui:field-wrapper>
			<liferay-ui:icon
				image="../api/exception"
				message=""
			/>

			<clay:link
				href="<%= patcherConfiguration.infoModifyTicketsListURL() %>"
				label="click-here-to-find-out-why-the-ticket-list-changed"
				target="_blank"
			/>
		</aui:field-wrapper>
	</aui:field-wrapper>
</c:if>

<portlet:renderURL var="viewPatcherAccountPatcherProductVersionURL">
	<portlet:param name="controller" value="accounts" />
	<portlet:param name="action" value="view" />
	<portlet:param name="patcherBuildAccountEntryCode" value="<%= patcherBuildAccountEntryCode %>" />
	<portlet:param name="patcherProductVersionId" value="<%= patcherBuild.patcherProductVersionId %>" />
</portlet:renderURL>

<aui:field-wrapper name="account-code">
	<clay:link
		href="<%= viewPatcherAccountPatcherProductVersionURL %>"
		title="<%= patcherBuildAccountEntryCode %>"
	/>
</aui:field-wrapper>

<aui:input inputCssClass="osb-patcher-input-wide osb-patcher-read-only" name="supportTicket" readonly="<%= true %>" type="text" />

<aui:select disabled="<%= true %>" name="type">
	<aui:option label="<%= PatcherBuildConstants.LABEL_OFFICIAL %>" value="<%= PatcherBuildConstants.TYPE_OFFICIAL %>" />
	<aui:option label="<%= PatcherBuildConstants.LABEL_DEBUG %>" value="<%= PatcherBuildConstants.TYPE_DEBUG %>" />
	<aui:option label="<%= PatcherBuildConstants.LABEL_IGNORE %>" value="<%= PatcherBuildConstants.TYPE_IGNORE %>" />
</aui:select>

<aui:input disabled="<%= true %>" name="mergeOnly" type="checkbox" value="<%= patcherBuildMergeOnly %>" />

<aui:button-row>
	<c:if test="<%= PatcherBuildUtil.isLatestPatcherBuild(patcherBuild) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK) && !PatcherBuildRelUtil.hasParentPatcherBuilds(patcherBuild) %>">
		<portlet:renderURL var="editPatcherBuildURL">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="edit" />
			<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
			<portlet:param name="redirect" value="<%= viewPatcherBuildURL %>" />
		</portlet:renderURL>

		<aui:button href="<%= editPatcherBuildURL %>" value="edit" />
	</c:if>

	<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) && !PatcherBuildRelUtil.hasChildPatcherBuilds(patcherBuild) %>">
		<portlet:renderURL var="viewPatcherBuildPatcherFixesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="fixes" />
			<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
		</portlet:renderURL>

		<c:set value='<%= UnicodeLanguageUtil.format(request, "view-fixes-for-build-id-x", patcherBuild.patcherBuildId) %>' var="viewPatcherFixesURLTitle" />

		<c:set value='<%= "javascript:Liferay.Patcher.openWindow('" + viewPatcherBuildPatcherFixesURL %>', '<%= viewPatcherFixesURLTitle + "', true, 1000);" %>' var="viewPatcherBuildPatcherFixesURL" />

		<aui:button onClick="<%= viewPatcherBuildPatcherFixesURL %>" value="view-fixes" />
	</c:if>

	<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.CHILD_BUILDS, patcherBuild.userId) && PatcherBuildRelUtil.hasChildPatcherBuilds(patcherBuild) %>">
		<portlet:renderURL var="viewChildPatcherBuildsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="childBuilds" />
			<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
		</portlet:renderURL>

		<c:set value='<%= UnicodeLanguageUtil.format(request, "view-child-builds-for-build-id-x", patcherBuild.patcherBuildId) %>' var="viewPatcherBuildsURLTitle" />

		<c:set value='<%= "javascript:Liferay.Patcher.openWindow('" + viewChildPatcherBuildsURL %>', '<%= viewPatcherBuildsURLTitle + "', true, 1000);" %>' var="viewChildPatcherBuildsURL" />

		<aui:button onClick="<%= viewChildPatcherBuildsURL %>" value="view-child-builds" />
	</c:if>

	<liferay-ui:icon-menu
		cssClass="osb-patcher-icon-menu"
	>
		<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.EDIT, patcherBuild.userId) && PatcherBuildUtil.isLatestPatcherBuild(patcherBuild) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK) %>">
			<portlet:renderURL var="createPatcherBuildTemplateURL">
				<portlet:param name="controller" value="builds" />
				<portlet:param name="action" value="create" />
				<portlet:param name="templatePatcherBuildId" value="<%= patcherBuild.patcherBuildId %>" />
				<portlet:param name="redirect" value="<%= viewPatcherBuildURL %>" />
			</portlet:renderURL>

			<liferay-ui:icon
				image="edit"
				message="use-as-build-template"
				method="get"
				url="<%= createPatcherBuildTemplateURL %>"
			/>
		</c:if>

		<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherBuild.userId) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK) %>">
			<portlet:renderURL var="editPatcherBuildCommentsFieldURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
				<portlet:param name="controller" value="builds" />
				<portlet:param name="action" value="editCommentsField" />
				<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
			</portlet:renderURL>

			<c:set value='<%= UnicodeLanguageUtil.format(request, "edit-engineer-comments-for-build-id-x", patcherBuild.patcherBuildId) %>' var="editPatcherBuildCommentsFieldURLTitle" />

			<c:set value='<%= "javascript:Liferay.Patcher.openWindow('" + editPatcherBuildCommentsFieldURL %>', '<%= editPatcherBuildCommentsFieldURLTitle + "', true, 1000);" %>' var="editPatcherBuildCommentsFieldURL" />

			<liferay-ui:icon
				image="edit"
				message="edit-engineer-comments"
				method="get"
				url="<%= editPatcherBuildCommentsFieldURL %>"
			/>
		</c:if>

		<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.EDIT_QA_FIELDS, patcherBuild.userId) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK) %>">
			<portlet:renderURL var="editPatcherBuildQAFieldsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
				<portlet:param name="controller" value="builds" />
				<portlet:param name="action" value="editQAFields" />
				<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
			</portlet:renderURL>

			<c:set value='<%= UnicodeLanguageUtil.format(request, "edit-qa-status-for-build-id-x", patcherBuild.patcherBuildId) %>' var="editPatcherBuildQAFieldsURLTitle" />

			<c:set value='<%= "javascript:Liferay.Patcher.openWindow('" + editPatcherBuildQAFieldsURL %>', '<%= editPatcherBuildQAFieldsURLTitle + "', true, 1000);" %>' var="editPatcherBuildQAFieldsURL" />

			<liferay-ui:icon
				image="edit"
				message="edit-qa-status"
				method="get"
				url="<%= editPatcherBuildQAFieldsURL %>"
			/>
		</c:if>

		<c:set value="<%= PatcherPermission.contains(permissionChecker, patcherBuild, PatcherActionKeys.SEND_REQUEST, patcherBuild.userId) %>" var="sendRequestPermission" />

		<c:set value="<%= JenkinsUtil.isValidJenkinsSetup() %>" var="isValidJenkinsSetup" />

		<c:set value="<%= JenkinsUtil.isValidSendDistJenkinsRequest(themeDisplay, patcherBuild) %>" var="isValidSendDistJenkinsRequest" />

		<c:if test="<%= sendRequestPermission && isValidJenkinsSetup && isValidSendDistJenkinsRequest && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK) %>">
			<portlet:actionURL var="buildPatcherBuildURL">
				<portlet:param name="controller" value="builds" />
				<portlet:param name="action" value="build" />
				<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
				<portlet:param name="redirect" value="<%= viewPatcherBuildURL %>" />
			</portlet:actionURL>

			<liferay-ui:icon
				image="post"
				message="build"
				method="get"
				url="<%= buildPatcherBuildURL %>"
			/>
		</c:if>

		<c:if test="<%= patcherBuild.status == WorkflowConstants.STATUS_BUILD_COMPLETE %>">
			<portlet:actionURL var="testPatcherBuildURL">
				<portlet:param name="controller" value="builds" />
				<portlet:param name="action" value="test" />
				<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
				<portlet:param name="redirect" value="<%= viewPatcherBuildURL %>" />
			</portlet:actionURL>

			<liferay-ui:icon
				image="post"
				message="test"
				method="get"
				url="<%= testPatcherBuildURL %>"
			/>

			<portlet:actionURL var="smokeTestPatcherBuildURL">
				<portlet:param name="controller" value="builds" />
				<portlet:param name="action" value="smokeTest" />
				<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
				<portlet:param name="redirect" value="<%= viewPatcherBuildURL %>" />
			</portlet:actionURL>

			<liferay-ui:icon
				image="post"
				message="smoke-test"
				method="get"
				url="<%= smokeTestPatcherBuildURL %>"
			/>
		</c:if>

		<c:if test="<%= patcherBuild.status == WorkflowConstants.STATUS_BUILD_COMPLETE %>">
			<c:set value='<%= LanguageUtil.get(request, "this-patch-has-not-passed-qa-testing-are-you-sure-this-patch-is-ready-for-release") %>' var="releaseConfirmMessage" />

			<c:if test="<%= PatcherBuildUtil.isTestingPassed(patcherBuild) %>">
				<c:set value='<%= LanguageUtil.get(request, "are-you-sure-this-patch-is-ready-for-release") %>' var="releaseConfirmMessage" />
			</c:if>

			<portlet:actionURL var="releasePatcherBuildURL">
				<portlet:param name="controller" value="builds" />
				<portlet:param name="action" value="readyForRelease" />
				<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
				<portlet:param name="redirect" value="<%= viewPatcherBuildURL %>" />
			</portlet:actionURL>

			<liferay-ui:icon
				image="post"
				message="ready-for-release"
				method="get"
				onClick='<%= renderResponse.namespace %>confirm('<%= releaseConfirmMessage %>', '<%= releasePatcherBuildURL + "');" %>'
				url="javascript:"
			/>
		</c:if>

		<c:if test="<%= (patcherBuild.status == WorkflowConstants.STATUS_BUILD_COMPLETE) || (patcherBuild.status == WorkflowConstants.STATUS_BUILD_READY_TO_RELEASE) %>">
			<c:set value='<%= LanguageUtil.get(request, "this-patch-has-not-passed-qa-testing-are-you-sure-you-want-to-release-this-patch-to-the-customer") %>' var="releaseConfirmMessage" />

			<c:if test="<%= PatcherBuildUtil.isTestingPassed(patcherBuild) %>">
				<c:set value='<%= LanguageUtil.get(request, "are-you-sure-you-want-to-release-this-patch-to-the-customer") %>' var="releaseConfirmMessage" />
			</c:if>

			<portlet:actionURL var="releasePatcherBuildURL">
				<portlet:param name="controller" value="builds" />
				<portlet:param name="action" value="releaseManually" />
				<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
				<portlet:param name="redirect" value="<%= viewPatcherBuildURL %>" />
			</portlet:actionURL>

			<liferay-ui:icon
				image="post"
				message="release-manually"
				method="get"
				onClick='<%= renderResponse.namespace %>confirm('<%= releaseConfirmMessage %>', '<%= releasePatcherBuildURL + "');" %>'
				url="javascript:"
			/>

			<portlet:actionURL var="releasePatcherBuildURL">
				<portlet:param name="controller" value="builds" />
				<portlet:param name="action" value="releaseToHelpCenter" />
				<portlet:param name="id" value="<%= patcherBuild.patcherBuildId %>" />
				<portlet:param name="redirect" value="<%= viewPatcherBuildURL %>" />
			</portlet:actionURL>

			<liferay-ui:icon
				image="post"
				message="release-to-help-center"
				method="get"
				onClick='<%= renderResponse.namespace %>confirm('<%= releaseConfirmMessage %>', '<%= releasePatcherBuildURL + "');" %>'
				url="javascript:"
			/>
		</c:if>
	</liferay-ui:icon-menu>
</aui:button-row>

<c:if test="<%= alloySearchResult.size > 1 %>">
	<aui:field-wrapper name="build-versions" />

	<liferay-ui:search-container
		iteratorURL="<%= alloySearchResult.portletURL %>"
		total="<%= alloySearchResult.size %>"
	>
		<liferay-ui:search-container-results
			results="<%= alloySearchResult.baseModels %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.osb.patcher.model.PatcherBuild"
			escapedModel="<%= true %>"
			keyProperty="patcherBuildId"
			modelVar="patcherBuildKeyVersion"
		>
			<c:if test="<%= patcherBuild.patcherBuildId == patcherBuildKeyVersion.patcherBuildId %>">
				<liferay-ui:search-container-row-parameter
					name="className"
					value="selected"
				/>
			</c:if>

			<portlet:renderURL var="viewPatcherBuildKeyVersionURL">
				<portlet:param name="controller" value="builds" />
				<portlet:param name="action" value="view" />
				<portlet:param name="id" value="<%= patcherBuildKeyVersion.patcherBuildId %>" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				href="<%= (patcherBuild.patcherBuildId != patcherBuildKeyVersion.patcherBuildId) ? viewPatcherBuildKeyVersionURL : null %>"
				name="build-id"
				property="patcherBuildId"
			/>

			<liferay-ui:search-container-column-text
				name="version"
				property="keyVersion"
			/>

			<liferay-ui:search-container-column-text
				cssClass="osb-patcher-user-display"
				name="created-by"
			>
				<liferay-ui:user-display
					displayStyle="<%= 1 %>"
					url="<%= PatcherUtil.getUserDisplayURL(themeDisplay, patcherBuildKeyVersion.getUserId()) %>"
					userId="<%= patcherBuildKeyVersion.userId %>"
					userName="<%= patcherBuildKeyVersion.userName %>"
				/>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="modified-date"
			>
				<fmt:formatDate
					type="both"
					value="<%= patcherBuildKeyVersion.modifiedDate %>"
				/>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="status"
				value='<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherBuildKeyVersion.getStatus())) + ">" %>'
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			paginate="<%= false %>"
		/>
	</liferay-ui:search-container>
</c:if>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />confirm',
		function(message, url) {
			if (confirm(message)) {
				window.location.href = url;
			}
		}
	);

	YUI().ready(
		'aui-popover',
		function(Y) {
			var align_points = [Y.WidgetPositionAlign.LC, Y.WidgetPositionAlign.RC];
			var tickets = document.getElementById('_1_WAR_osbpatcherportlet_patcherBuildName');
			var trigger = Y.one('#_1_WAR_osbpatcherportlet_patcherBuildName');

			Liferay.Patcher.getTicketLinksPopover(Y, align_points, tickets, trigger)
		}
	);
</aui:script>