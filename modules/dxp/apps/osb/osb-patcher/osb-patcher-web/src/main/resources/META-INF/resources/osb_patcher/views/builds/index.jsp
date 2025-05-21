<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="qa-builds" />
</liferay-util:include>

<portlet:renderURL var="viewPatcherBuildsURL">
	<portlet:param name="controller" value="builds" />
	<portlet:param name="action" value="index" />
</portlet:renderURL>

<aui:form action="${viewPatcherBuildsURL}" method="get" name="fm">
	<liferay-ui:search-toggle
		buttonLabel="search"
		displayTerms="${displayTerms}"
		id="toggle_id_patcher_build_search"
	>
		<aui:input name="patcherProductVersionId" type="hidden" value="${patcherProductVersionId}" />

		<aui:layout>
			<aui:column>
				<aui:input label="build-id" name="${Field.ENTRY_CLASS_PK}" size="30" type="text" />
			</aui:column>

			<aui:column>
				<aui:input label="name" name="patcherBuildName" size="30" title="search-builds" type="text" />
			</aui:column>

			<aui:column>
				<aui:input label="account-code" name="patcherBuildAccountEntryCode" size="30" title="search-accounts" type="text" />
			</aui:column>

			<aui:column>
				<aui:input name="supportTicket" size="30" title="search-support-tickets" type="text" />
			</aui:column>

			<aui:column>
				<aui:input checked="${true}" name="hideOldBuildVersions" type="checkbox" />
			</aui:column>
		</aui:layout>

		<aui:layout>
			<aui:column>
				<aui:select label="status" name="statusFilter" showEmptyOption="${true}">
					<aui:option label="${WorkflowConstants.LABEL_BUILD_MERGING}" value="${WorkflowConstants.STATUS_BUILD_MERGING}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_COMPILING}" value="${WorkflowConstants.STATUS_BUILD_COMPILING}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_CONFLICT}" value="${WorkflowConstants.STATUS_BUILD_CONFLICT}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_COMPLETE}" value="${WorkflowConstants.STATUS_BUILD_COMPLETE}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_READY_TO_RELEASE}" value="${WorkflowConstants.STATUS_BUILD_READY_TO_RELEASE}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_RELEASED}" value="${WorkflowConstants.STATUS_BUILD_RELEASED}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_FAILED}" value="${WorkflowConstants.STATUS_BUILD_FAILED}" />
				</aui:select>
			</aui:column>

			<aui:column>
				<aui:select label="qa-status" name="qaStatusFilter" showEmptyOption="${true}">
					<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_NEEDED}" value="${WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_STARTED}" value="${WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_STARTED}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_PASSED}" value="${WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_STARTED}" value="${WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_FAILED_MANUALLY}" value="${WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_PASSED_MANUALLY}" value="${WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY}" />
					<aui:option label="${WorkflowConstants.LABEL_PENDING}" value="${WorkflowConstants.STATUS_PENDING}" />
					<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_PENDING_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_PENDING_SMOKE_ONLY}" />
				</aui:select>
			</aui:column>

			<aui:column>
				<aui:select label="type" name="typeFilter" showEmptyOption="${false}">
					<aui:option label="${PatcherBuildConstants.LABEL_OFFICIAL}" value="${PatcherBuildConstants.TYPE_OFFICIAL}" />
					<aui:option label="${PatcherBuildConstants.LABEL_DEBUG}" value="${PatcherBuildConstants.TYPE_DEBUG}" />
					<aui:option label="${PatcherBuildConstants.LABEL_IGNORE}" value="${PatcherBuildConstants.TYPE_IGNORE}" />
					<aui:option label="${PatcherBuildConstants.LABEL_FIX_PACK}" value="${PatcherBuildConstants.TYPE_FIX_PACK}" />
				</aui:select>
			</aui:column>

			<aui:column>
				<aui:select label="project-version" name="patcherProjectVersionIdFilter" showEmptyOption="${true}">
					<c:forEach items="${patcherProjectVersions}" var="patcherProjectVersion">
						<aui:option label="${patcherProjectVersion.name}" value="${patcherProjectVersion.patcherProjectVersionId}" />
					</c:forEach>
				</aui:select>
			</aui:column>
		</aui:layout>
	</liferay-ui:search-toggle>
</aui:form>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-builds"
	iteratorURL="${alloySearchResult.portletURL}"
>
	<liferay-ui:search-container-results
		results="${alloySearchResult.baseModels}"
		total="${alloySearchResult.size}"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherBuild"
		escapedModel="${true}"
		keyProperty="patcherBuildId"
		modelVar="patcherBuild"
	>
		<liferay-ui:search-container-row-parameter
			name="className"
			value="patcher-build-type-${PatcherBuildConstantsMethods.getTypeLabel(patcherBuild.type)}"
		/>

		<portlet:renderURL var="viewPatcherBuildPatcherFixesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="fixes" />
			<portlet:param name="id" value="${patcherBuild.patcherBuildId}" />
		</portlet:renderURL>

		<c:set value='${AlloyLanguageUtil.formatUnicode("view-fixes-for-build-id-x", patcherBuild.patcherBuildId)}' var="viewPatcherFixesURLTitle" />

		<c:set value="javascript:Liferay.Patcher.openWindow('${viewPatcherBuildPatcherFixesURL}', '${viewPatcherFixesURLTitle}', true, 1000);" var="viewPatcherBuildPatcherFixesURL" />

		<c:set value='${PatcherPermission.contains(themeDisplay, patcherBuild, "fixes")}' var="fixes" />

		<liferay-ui:search-container-column-text
			cssClass="osb-patcher-search-container-column-text-icon"
		>
			<liferay-ui:icon
				image='${(PatcherBuildUtil.isObsolete(patcherBuild.patcherBuildId)) ? "../common/activate" : StringPool.BLANK}'
				message="this-build-is-obsolete"
				url="${(fixes) ? viewPatcherBuildPatcherFixesURL : StringPool.BLANK}"
			/>

			<liferay-ui:icon
				image='${(PatcherFixUtil.containsPatcherFixWorkaround(patcherBuild.patcherBuildId)) ? "../api/exception" : StringPool.BLANK}'
				message="this-build-contains-workaround-fixes"
				url="${(fixes) ? viewPatcherBuildPatcherFixesURL : StringPool.BLANK}"
			/>

			<liferay-ui:icon
				image='${(PatcherFixUtil.containsPatcherFixComment(patcherBuild.patcherBuildId)) ? "../common/message" : StringPool.BLANK}'
				message="this-build-contains-fixes-with-comments"
				url="${(fixes) ? viewPatcherBuildPatcherFixesURL : StringPool.BLANK}"
			/>
		</liferay-ui:search-container-column-text>

		<portlet:renderURL var="viewPatcherBuildURL">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="view" />
			<portlet:param name="id" value="${patcherBuild.patcherBuildId}" />
			<portlet:param name="redirect" value="${viewPatcherAccountURL}" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			href="${viewPatcherBuildURL}"
			name="build-id"
			property="patcherBuildId"
		/>

		<liferay-ui:search-container-column-text
			name="type"
			value="${AlloyLanguageUtil.format(PatcherBuildConstantsMethods.getTypeLabel(patcherBuild.getType()))}"
		/>

		<liferay-ui:search-container-column-text
			name="account-code"
			value="${PatcherBuildUtil.getPatcherAccountEntryCode(patcherBuild.getPatcherAccountId())}"
		/>

		<liferay-ui:search-container-column-text
			cssClass="nobr"
			href="${PatcherBuildUtil.getSupportTicketURL(patcherBuild.supportTicket)}"
			name="support-ticket"
			target="_blank"
			value="${patcherBuild.supportTicket}"
		/>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<c:set value="${PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherBuild.getPatcherProjectVersionId())}" var="patcherProjectVersion" />

		<liferay-ui:search-container-column-text
			name="product-version"
			value="${PatcherProductVersionUtil.fetchPatcherProductVersionName(patcherProjectVersion.getPatcherProductVersionId())}"
		/>

		<liferay-ui:search-container-column-text
			name="project-version"
			value="${patcherProjectVersion.name}"
		/>

		<liferay-ui:search-container-column-text
			name="content"
		>
			<portlet:renderURL var="viewPatcherBuildContentURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
				<portlet:param name="controller" value="builds" />
				<portlet:param name="action" value="content" />
				<portlet:param name="id" value="${patcherBuild.patcherBuildId}" />
			</portlet:renderURL>

			<c:set value='${AlloyLanguageUtil.formatUnicode("content")}' var="viewPatcherBuildContentURLTitle" />

			<c:set value="javascript:Liferay.Patcher.openWindow('${viewPatcherBuildContentURL}', '${viewPatcherBuildContentURLTitle}', true, 1000, 1);" var="viewPatcherBuildContentURL" />

			<c:set value="${patcherBuild.getName()}" var="patcherBuildName" />

			<c:set value="${PatcherFixPackUtil.getPatcherFixPackNamesCount(patcherBuildName)}" var="patcherFixPackNamesCount" />

			<c:set value='${AlloyLanguageUtil.format("fix-packs")}' var="fixPacksLabel" />

			<c:set value="${PatcherUtil.getTicketsCount(patcherBuildName)}" var="ticketsCount" />

			<c:set value='${AlloyLanguageUtil.format("tickets")}' var="ticketsLabel" />

			<aui:a class="nobr" href="${viewPatcherBuildContentURL}" title="${patcherBuildName}">
				${patcherFixPackNamesCount} ${fixPacksLabel} + ${ticketsCount} ${ticketsLabel}
			</aui:a>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="status-date"
		>
			<fmt:formatDate
				value="${patcherBuild.statusDate}"
			/>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="patcher-status"
			value="${AlloyLanguageUtil.format(WorkflowConstantsMethods.getStatusLabel(patcherBuild.getStatus()))}"
		/>

		<liferay-ui:search-container-column-text
			name="jenkins"
		>
			<c:set value="${JenkinsUtil.getJenkinsResults(patcherBuild)}" var="jenkinsResults" />

			<c:forEach items="${jenkinsResults}" var="jenkinsResult">
				<aui:a cssClass="nobr" href="${jenkinsResult.statusURL}" label="${jenkinsResult.jobName}" target="_blank" />
			</c:forEach>
		</liferay-ui:search-container-column-text>

		<portlet:renderURL var="editPatcherBuildCommentsFieldURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="editCommentsField" />
			<portlet:param name="id" value="${patcherBuild.patcherBuildId}" />
		</portlet:renderURL>

		<c:set value='${AlloyLanguageUtil.formatUnicode("edit-engineer-comments-for-build-id-x", patcherBuild.patcherBuildId)}' var="editPatcherBuildCommentsFieldURLTitle" />

		<c:set value="javascript:Liferay.Patcher.openWindow('${editPatcherBuildCommentsFieldURL}', '${editPatcherBuildCommentsFieldURLTitle}', true, 800)" var="editPatcherBuildCommentsFieldURL" />

		<liferay-ui:search-container-column-text
			name="engineer-comments"
		>
			<c:set value="${StringUtil.shorten(patcherBuild.comments, 75)}" var="shortenedPatcherBuildComments" />

			<c:choose>
				<c:when test='${PatcherPermission.contains(themeDisplay, patcherBuild, "editCommentsField") && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK)}'>
					<aui:a href="${editPatcherBuildCommentsFieldURL}">
						${shortenedPatcherBuildComments}
					</aui:a>
				</c:when>
				<c:otherwise>
					${shortenedPatcherBuildComments}
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="qa-status"
			value="${AlloyLanguageUtil.format(WorkflowConstantsMethods.getStatusLabel(patcherBuild.getQaStatus()))}"
		/>

		<portlet:renderURL var="editPatcherBuildQAFieldsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="editQAFields" />
			<portlet:param name="id" value="${patcherBuild.patcherBuildId}" />
		</portlet:renderURL>

		<c:set value='${AlloyLanguageUtil.formatUnicode("edit-qa-status-for-build-id-x", patcherBuild.patcherBuildId)}' var="editPatcherBuildQAFieldsURLTitle" />

		<c:set value="javascript:Liferay.Patcher.openWindow('${editPatcherBuildQAFieldsURL}', '${editPatcherBuildQAFieldsURLTitle}', true, 800)" var="editPatcherBuildQAFieldsURL" />

		<liferay-ui:search-container-column-text
			name="qa-comments"
		>
			<c:set value="${StringUtil.shorten(patcherBuild.qaComments, 75)}" var="shortenedPatcherBuildQAComments" />

			<c:choose>
				<c:when test='${PatcherPermission.contains(themeDisplay, patcherBuild, "editQAFields") && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK)}'>
					<aui:a href="${editPatcherBuildQAFieldsURL}">
						${shortenedPatcherBuildQAComments}
					</aui:a>
				</c:when>
				<c:otherwise>
					${shortenedPatcherBuildQAComments}
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			cssClass="nobr"
			href='${patcherBuild.fileName.contains("/liferay-dxp-") ? "https://releases-cdn.liferay.com/dxp/hotfix" : PortletPropsValues.OSB_PATCHER_BUILD_DOWNLOAD_URL}/${patcherBuild.fileName}'
			name="hotfix"
			value="${PatcherBuildUtil.isCompleteReadyOrReleased(patcherBuild) ? PatcherBuildUtil.getLiferayHotfixFileName(patcherBuild.fileName) : StringPool.BLANK}"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu
				cssClass="osb-patcher-icon-menu"
			>
				<c:if test='${PatcherPermission.contains(themeDisplay, patcherBuild, "edit") && PatcherBuildUtil.isLatestPatcherBuild(patcherBuild) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK)}'>
					<portlet:renderURL var="editPatcherBuildURL">
						<portlet:param name="controller" value="builds" />
						<portlet:param name="action" value="edit" />
						<portlet:param name="id" value="${patcherBuild.patcherBuildId}" />
						<portlet:param name="redirect" value="${viewPatcherAccountURL}" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="${editPatcherBuildURL}"
					/>
				</c:if>

				<c:if test='${PatcherPermission.contains(themeDisplay, patcherBuild, "edit") && PatcherBuildUtil.isLatestPatcherBuild(patcherBuild) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK)}'>
					<portlet:renderURL var="createPatcherBuildTemplateURL">
						<portlet:param name="controller" value="builds" />
						<portlet:param name="action" value="create" />
						<portlet:param name="templatePatcherBuildId" value="${patcherBuild.patcherBuildId}" />
						<portlet:param name="redirect" value="${viewPatcherAccountURL}" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						message="use-as-build-template"
						method="get"
						url="${createPatcherBuildTemplateURL}"
					/>
				</c:if>

				<c:if test='${PatcherPermission.contains(themeDisplay, patcherBuild, "editCommentsField") && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK)}'>
					<liferay-ui:icon
						image="edit"
						message="edit-engineer-comments"
						method="get"
						url="${editPatcherBuildCommentsFieldURL}"
					/>
				</c:if>

				<c:if test='${PatcherPermission.contains(themeDisplay, patcherBuild, "editQAFields") && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK)}'>
					<liferay-ui:icon
						image="edit"
						message="edit-qa-status"
						method="get"
						url="${editPatcherBuildQAFieldsURL}"
					/>
				</c:if>

				<c:set value='${PatcherPermission.contains(themeDisplay, patcherBuild, "sendRequest")}' var="sendRequestPermission" />

				<c:set value="${JenkinsUtil.isValidJenkinsSetup()}" var="isValidJenkinsSetup" />

				<c:set value="${JenkinsUtil.isValidSendDistJenkinsRequest(themeDisplay, patcherBuild)}" var="isValidSendDistJenkinsRequest" />

				<c:if test="${sendRequestPermission && isValidJenkinsSetup && isValidSendDistJenkinsRequest && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK)}">
					<portlet:actionURL var="buildPatcherBuildURL">
						<portlet:param name="controller" value="builds" />
						<portlet:param name="action" value="build" />
						<portlet:param name="id" value="${patcherBuild.patcherBuildId}" />
						<portlet:param name="redirect" value="${alloySearchResult.portletURL}" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="build"
						method="get"
						url="${buildPatcherBuildURL}"
					/>
				</c:if>

				<c:if test="${patcherBuild.status == WorkflowConstants.STATUS_BUILD_COMPLETE}">
					<portlet:actionURL var="testPatcherBuildURL">
						<portlet:param name="controller" value="builds" />
						<portlet:param name="action" value="test" />
						<portlet:param name="id" value="${patcherBuild.patcherBuildId}" />
						<portlet:param name="redirect" value="${alloySearchResult.portletURL}" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="test"
						method="get"
						url="${testPatcherBuildURL}"
					/>

					<portlet:actionURL var="smokeTestPatcherBuildURL">
						<portlet:param name="controller" value="builds" />
						<portlet:param name="action" value="smokeTest" />
						<portlet:param name="id" value="${patcherBuild.patcherBuildId}" />
						<portlet:param name="redirect" value="${alloySearchResult.portletURL}" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="post"
						message="smoke-test"
						method="get"
						url="${smokeTestPatcherBuildURL}"
					/>
				</c:if>

				<c:if test='${PatcherPermission.contains(themeDisplay, patcherBuild, "fixes") && !PatcherBuildRelUtil.hasChildPatcherBuilds(patcherBuild)}'>
					<liferay-ui:icon
						image="view"
						message="view-fixes"
						method="get"
						url="${viewPatcherBuildPatcherFixesURL}"
					/>
				</c:if>

				<c:if test='${PatcherPermission.contains(themeDisplay, patcherBuild, "childBuilds") && PatcherBuildRelUtil.hasChildPatcherBuilds(patcherBuild)}'>
					<portlet:renderURL var="viewChildPatcherBuildsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="controller" value="builds" />
						<portlet:param name="action" value="childBuilds" />
						<portlet:param name="id" value="${patcherBuild.patcherBuildId}" />
					</portlet:renderURL>

					<c:set value='${AlloyLanguageUtil.formatUnicode("view-child-builds-for-build-id-x", patcherBuild.patcherBuildId)}' var="viewPatcherBuildsURLTitle" />

					<c:set value="javascript:Liferay.Patcher.openWindow('${viewChildPatcherBuildsURL}', '${viewPatcherBuildsURLTitle}', true, 1000);" var="viewChildPatcherBuildsURL" />

					<liferay-ui:icon
						image="view"
						message="view-child-builds"
						method="get"
						url="${viewChildPatcherBuildsURL}"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>