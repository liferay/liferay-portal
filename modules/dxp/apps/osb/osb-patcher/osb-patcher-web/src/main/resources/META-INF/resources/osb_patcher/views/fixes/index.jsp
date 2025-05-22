<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="fixes" />
</liferay-util:include>

<aui:layout>
	<clay:col>
		<aui:select label="product-version" name="patcherProductVersionId" onChange="${renderResponse.namespace}productVersionOnChange(this.value);" showEmptyOption="${true}">
			<c:forEach items="${patcherProductVersions}" var="patcherProductVersion">
				<aui:option label="${patcherProductVersion.name}" value="${patcherProductVersion.patcherProductVersionId}" />
			</c:forEach>

			<aui:option label="any" value="0" />
		</aui:select>
	</clay:col>
</aui:layout>

<aui:button-row>
	<portlet:renderURL var="createPatcherFixURL">
		<portlet:param name="controller" value="fixes" />
		<portlet:param name="action" value="create" />
		<portlet:param name="patcherProductVersionId" value="${patcherProductVersionId}" />
	</portlet:renderURL>

	<aui:button disabled='${!PatcherPermission.contains(themeDisplay, "fixes", PatcherActionKeys.CREATE)}' href="${createPatcherFixURL}" value="create-fix" />
</aui:button-row>

<portlet:renderURL var="viewPatcherFixesURL">
	<portlet:param name="controller" value="fixes" />
	<portlet:param name="action" value="index" />
</portlet:renderURL>

<aui:form action="${viewPatcherFixesURL}" method="get" name="fm">
	<aui:input name="patcherProductVersionId" type="hidden" value="${patcherProductVersionId}" />

	<liferay-ui:search-toggle
		buttonLabel="search"
		displayTerms="${displayTerms}"
		id="toggle_id_patcher_fix_search"
	>
		<aui:layout>
			<clay:col>
				<aui:input label="fix-id" name="${Field.ENTRY_CLASS_PK}" size="30" type="text" />
			</clay:col>

			<clay:col>
				<aui:input label="content" name="patcherFixName" size="30" title="search-fixes" type="text" />
			</clay:col>

			<clay:col>
				<aui:input checked="${true}" inlineField="${false}" name="hideOldFixVersions" type="checkbox" />
			</clay:col>
		</aui:layout>

		<aui:layout>
			<clay:col>
				<aui:select label="patcher-status" name="statusFilter" showEmptyOption="${true}">
					<aui:option label="${WorkflowConstants.LABEL_FIX_ADDING}" value="${WorkflowConstants.STATUS_FIX_ADDING}" />
					<aui:option label="${WorkflowConstants.LABEL_FIX_CONFLICT}" value="${WorkflowConstants.STATUS_FIX_CONFLICT}" />
					<aui:option label="${WorkflowConstants.LABEL_FIX_COMPLETE}" value="${WorkflowConstants.STATUS_FIX_COMPLETE}" />
					<aui:option label="${WorkflowConstants.LABEL_FIX_FAILED}" value="${WorkflowConstants.STATUS_FIX_FAILED}" />
					<aui:option label="${WorkflowConstants.LABEL_FIX_REBASE_CONFLICT}" value="${WorkflowConstants.STATUS_FIX_REBASE_CONFLICT}" />
					<aui:option label="${WorkflowConstants.LABEL_FIX_REBASING}" value="${WorkflowConstants.STATUS_FIX_REBASING}" />
				</aui:select>
			</clay:col>

			<clay:col>
				<aui:select label="type" name="typeFilter" showEmptyOption="${true}">
					<aui:option label="${PatcherFixConstants.LABEL_PATCH}" value="${PatcherFixConstants.TYPE_PATCH}" />
					<aui:option label="${PatcherFixConstants.LABEL_WORKAROUND}" value="${PatcherFixConstants.TYPE_WORKAROUND}" />
					<aui:option label="${PatcherFixConstants.LABEL_FIX_PACK}" value="${PatcherFixConstants.TYPE_FIX_PACK}" />
					<aui:option label="${PatcherFixConstants.LABEL_GENERATED}" value="${PatcherFixConstants.TYPE_GENERATED}" />
					<aui:option label="${PatcherFixConstants.LABEL_GENERATED_PRIVATE_PUBLIC}" value="${PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC}" />
					<aui:option label="${PatcherFixConstants.LABEL_EXCLUDED}" value="${PatcherFixConstants.TYPE_EXCLUDED}" />
					<aui:option label="${PatcherFixConstants.LABEL_REBASE}" value="${PatcherFixConstants.TYPE_REBASE}" />
				</aui:select>
			</clay:col>

			<clay:col>
				<aui:select label="project-version" name="patcherProjectVersionIdFilter" showEmptyOption="${true}">
					<c:forEach items="${patcherProjectVersions}" var="patcherProjectVersion">
						<aui:option label="${patcherProjectVersion.name}" value="${patcherProjectVersion.patcherProjectVersionId}" />
					</c:forEach>
				</aui:select>
			</clay:col>
		</aui:layout>
	</liferay-ui:search-toggle>
</aui:form>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-fixes"
	iteratorURL="${alloySearchResult.portletURL}"
>
	<liferay-ui:search-container-results
		results="${alloySearchResult.baseModels}"
		total="${alloySearchResult.size}"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherFix"
		escapedModel="${true}"
		keyProperty="patcherFixId"
		modelVar="patcherFix"
	>
		<portlet:renderURL var="viewPatcherFixPopUpURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="fixes" />
			<portlet:param name="action" value="view" />
			<portlet:param name="id" value="${patcherFix.patcherFixId}" />
		</portlet:renderURL>

		<c:set value='${AlloyLanguageUtil.format("view-fix")}' var="viewPatcherFixPopUpTitle" />

		<c:set value='javascript:Liferay.Patcher.openWindow("${viewPatcherFixPopUpURL}", "${viewPatcherFixPopUpTitle}", true, 1000)' var="viewPatcherFixPopUpURL" />

		<liferay-ui:search-container-column-text
			cssClass="osb-patcher-search-container-column-text-icon"
		>
			<liferay-ui:icon
				image='${(patcherFix.obsolete) ? "../common/activate" : StringPool.BLANK }'
				message="this-fix-is-obsolete"
				url="${viewPatcherFixPopUpURL}"
			/>
		</liferay-ui:search-container-column-text>

		<portlet:renderURL var="viewPatcherFixURL">
			<portlet:param name="controller" value="fixes" />
			<portlet:param name="action" value="view" />
			<portlet:param name="id" value="${patcherFix.patcherFixId}" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			href="${viewPatcherFixURL}"
			name="fix-id"
			property="patcherFixId"
		/>

		<liferay-ui:search-container-column-text
			name="content"
		>
			<c:set value="${StringUtil.split(patcherFix.getName())}" var="tokens" />

			<c:forEach items="${tokens}" var="token" varStatus="tokenStatus">
				<c:choose>
					<c:when test="${PatcherFixPackUtil.containsPatcherFixPackName(token)}">
						<c:set value="${PatcherFixPackUtil.getPatcherFixPack(token, patcherFix.getPatcherProjectVersionId())}" var="patcherFixPack" />

						<portlet:renderURL var="viewPatcherFixPackURL">
							<portlet:param name="controller" value="fix_packs" />
							<portlet:param name="action" value="view" />
							<portlet:param name="id" value="${patcherFixPack.patcherFixPackId}" />
						</portlet:renderURL>

						<a class="nobr" href="${viewPatcherFixPackURL}">${token}</a>${(!tokenStatus.last) ? StringPool.COMMA : StringPool.BLANK}
					</c:when>
					<c:otherwise>
						<a class="nobr" href="${PortletPropsValues.JIRA_URL}/${token}" target="_blank">${token}</a>${(!tokenStatus.last) ? StringPool.COMMA : StringPool.BLANK}
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<c:set value="${PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherFix.getPatcherProjectVersionId())}" var="patcherProjectVersion" />

		<liferay-ui:search-container-column-text
			name="product-version"
			value="${PatcherProductVersionUtil.fetchPatcherProductVersionName(patcherProjectVersion.getPatcherProductVersionId())}"
		/>

		<liferay-ui:search-container-column-text
			name="project-version"
			value="${patcherProjectVersion.name}"
		/>

		<liferay-ui:search-container-column-text
			href="${PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId())}"
			name="git-hash"
			target="_blank"
			value="${fn:substring(patcherFix.gitHash, 0, 10)}"
		/>

		<liferay-ui:search-container-column-text
			name="patcher-status"
			value="${AlloyLanguageUtil.format(WorkflowConstantsMethods.getStatusLabel(patcherFix.getStatus()))}"
		/>

		<portlet:renderURL var="editPatcherFixCommentsFieldURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="fixes" />
			<portlet:param name="action" value="editCommentsField" />
			<portlet:param name="id" value="${patcherFix.patcherFixId}" />
		</portlet:renderURL>

		<c:set value='${AlloyLanguageUtil.formatUnicode("edit-engineer-comments-for-fix-id-x", patcherFix.patcherFixId)}' var="editPatcherFixCommentsFieldURLTitle" />

		<c:set value="javascript:Liferay.Patcher.openWindow('${editPatcherFixCommentsFieldURL}', '${editPatcherFixCommentsFieldURLTitle}', true, 800)" var="editPatcherFixCommentsFieldURL" />

		<c:set value="${StringUtil.shorten(patcherFix.comments, 75)}" var="shortenedPatcherFixComments" />

		<liferay-ui:search-container-column-text
			name="engineer-comments"
		>
			<c:choose>
				<c:when test="${PatcherPermission.contains(themeDisplay, patcherFix, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherFix.userId) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK)}">
					<aui:a href="${editPatcherFixCommentsFieldURL}">
						${shortenedPatcherFixComments}
					</aui:a>
				</c:when>
				<c:otherwise>
					${shortenedPatcherFixComments}
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="jenkins"
		>
			<c:set value="${JenkinsUtil.getJenkinsResults(patcherFix)}" var="jenkinsResults" />

			<c:forEach items="${jenkinsResults}" var="jenkinsResult">
				<aui:a cssClass="nobr" href="${jenkinsResult.statusURL}" label="${jenkinsResult.jobName}" target="_blank" />
			</c:forEach>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="type"
			value="${AlloyLanguageUtil.format(PatcherFixConstantsMethods.getTypeLabel(patcherFix.getType()))}"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu
				cssClass="osb-patcher-icon-menu"
			>
				<c:if test="${PatcherPermission.contains(themeDisplay, patcherFix, PatcherActionKeys.EDIT, patcherFix.userId) && patcherFix.latestFix && (patcherFix.type != PatcherFixConstants.TYPE_FIX_PACK)}">
					<portlet:renderURL var="editPatcherFixURL">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="edit" />
						<portlet:param name="id" value="${patcherFix.patcherFixId}" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="${editPatcherFixURL}"
					/>
				</c:if>

				<c:if test="${PatcherPermission.contains(themeDisplay, patcherFix, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherFix.userId) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK)}">
					<liferay-ui:icon
						image="edit"
						message="edit-engineer-comments"
						method="get"
						url="${editPatcherFixCommentsFieldURL}"
					/>
				</c:if>

				<c:if test="${PatcherPermission.contains(themeDisplay, patcherFix, PatcherActionKeys.EDIT_FIX_PACK_FIELDS, patcherFix.userId) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK)}">
					<portlet:renderURL var="editPatcherFixFixPackFieldsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="editFixPackFields" />
						<portlet:param name="id" value="${patcherFix.patcherFixId}" />
					</portlet:renderURL>

					<c:set value='${AlloyLanguageUtil.formatUnicode("edit-fix-packs")}' var="editPatcherFixFixPackFieldsURLTitle" />

					<c:set value="javascript:Liferay.Patcher.openWindow('${editPatcherFixFixPackFieldsURL}', '${editPatcherFixFixPackFieldsURLTitle}', true, 800)" var="editPatcherFixFixPackFieldsURL" />

					<liferay-ui:icon
						image="edit"
						message="edit-fix-packs"
						method="get"
						url="${editPatcherFixFixPackFieldsURL}"
					/>
				</c:if>

				<c:if test="${PatcherPermission.contains(themeDisplay, patcherFix, PatcherActionKeys.BUILDS, patcherFix.userId)}">
					<portlet:renderURL var="viewPatcherFixPatcherBuildsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="builds" />
						<portlet:param name="id" value="${patcherFix.patcherFixId}" />
					</portlet:renderURL>

					<c:set value='${AlloyLanguageUtil.formatUnicode("view-builds-for-fix-id-x", patcherFix.patcherFixId)}' var="viewPatcherBuildsURLTitle" />

					<c:set value="javascript:Liferay.Patcher.openWindow('${viewPatcherFixPatcherBuildsURL}', '${viewPatcherBuildsURLTitle}', true, 1000);" var="viewPatcherFixPatcherBuildsURL" />

					<liferay-ui:icon
						image="view"
						message="view-builds"
						method="get"
						url="${viewPatcherFixPatcherBuildsURL}"
					/>
				</c:if>

				<c:if test="${PatcherPermission.contains(themeDisplay, patcherFix, PatcherActionKeys.FIXES, patcherFix.userId)}">
					<portlet:renderURL var="viewPatcherFixesPopUpURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="fixes" />
						<portlet:param name="id" value="${patcherFix.patcherFixId}" />
					</portlet:renderURL>

					<c:set value='${AlloyLanguageUtil.formatUnicode("view-fixes-for-fix-id-x", patcherFix.patcherFixId)}' var="viewPatcherFixesPopUpURLTitle" />

					<c:set value="javascript:Liferay.Patcher.openWindow('${viewPatcherFixesPopUpURL}', '${viewPatcherFixesPopUpURLTitle}', true, 1000);" var="viewPatcherFixesPopUpURL" />

					<liferay-ui:icon
						image="view"
						message="view-fixes"
						method="get"
						url="${viewPatcherFixesPopUpURL}"
					/>
				</c:if>

				<c:if test="${false && (patcherFix.type != PatcherFixConstants.TYPE_FIX_PACK)}">
					<portlet:actionURL var="deletePatcherFixURL">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="delete" />
						<portlet:param name="id" value="${patcherFix.patcherFixId}" />
						<portlet:param name="redirect" value="${alloySearchResult.portletURL}" />
					</portlet:actionURL>

					<liferay-ui:icon-delete
						url="${deletePatcherFixURL}"
					/>
				</c:if>

				<c:if test="${PatcherPermission.contains(themeDisplay, patcherFix, PatcherActionKeys.EXCLUDE, patcherFix.userId) && (patcherFix.type != PatcherFixConstants.TYPE_EXCLUDED) && (patcherFix.type != PatcherFixConstants.TYPE_FIX_PACK)}">
					<portlet:actionURL var="excludePatcherFixURL">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="exclude" />
						<portlet:param name="id" value="${patcherFix.patcherFixId}" />
						<portlet:param name="redirect" value="${alloySearchResult.portletURL}" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="../api/method"
						message="exclude"
						url="${excludePatcherFixURL}"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />productVersionOnChange',
		function(productVersionId) {
			var namespace = '<portlet:namespace />';

			window.location.href = Liferay.Patcher.updateProductVersionId('${alloySearchResult.portletURL}', productVersionId, namespace);
		},
		['aui-base']
	);
</aui:script>