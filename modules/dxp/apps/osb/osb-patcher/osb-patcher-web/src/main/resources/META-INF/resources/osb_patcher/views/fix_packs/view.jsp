<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="fix-packs" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="view-fix-pack" />
	<liferay-util:param name="controller" value="fix_packs" />
	<liferay-util:param name="action" value="index" />
</liferay-util:include>

<c:if test="${not empty mainPatcherFix.gitHash}">
	<aui:field-wrapper name="git-hash">
		<a href="${gitHubURL}" target="_blank">${mainPatcherFix.gitHash}</a>
	</aui:field-wrapper>
</c:if>

<aui:field-wrapper name="name">
	${patcherFixPack.name}
</aui:field-wrapper>

<aui:field-wrapper name="component">
	${patcherFixComponent.name}
</aui:field-wrapper>

<aui:field-wrapper name="version">
	${patcherFixPack.version}
</aui:field-wrapper>

<aui:field-wrapper name="status">
	${patcherFixPackStatus}
</aui:field-wrapper>

<aui:field-wrapper name="build-status">
	${patcherBuildStatus}
</aui:field-wrapper>

<c:if test="${not empty mainPatcherBuild.qaStatus}">
	<aui:field-wrapper name="qa-status">
		${patcherBuildQAStatus}
	</aui:field-wrapper>
</c:if>

<aui:field-wrapper name="released-date">
	<fmt:formatDate
		value="${patcherFixPack.releasedDate}"
	/>
</aui:field-wrapper>

<aui:field-wrapper name="requirements">
	${patcherFixPack.requirements}
</aui:field-wrapper>

<aui:field-wrapper name="new-issues">
	${newTickets}
</aui:field-wrapper>

<aui:field-wrapper name="overridden-issues">
	${overriddenTickets}
</aui:field-wrapper>

<aui:field-wrapper name="oldest-fix-date">
	<fmt:formatDate
		value="${oldestPatcherFixDate}"
	/>
</aui:field-wrapper>

<aui:button-row>
	<portlet:renderURL var="viewPatcherFixPackURL">
		<portlet:param name="controller" value="fix_packs" />
		<portlet:param name="action" value="view" />
		<portlet:param name="id" value="${patcherFixPack.patcherFixPackId}" />

		<c:if test="${not empty redirect}">
			<portlet:param name="redirect" value="${redirect}" />
		</c:if>
	</portlet:renderURL>

	<portlet:renderURL var="editPatcherFixPackURL">
		<portlet:param name="controller" value="fix_packs" />
		<portlet:param name="action" value="edit" />
		<portlet:param name="id" value="${patcherFixPack.patcherFixPackId}" />
		<portlet:param name="redirect" value="${viewPatcherFixPackURL}" />
	</portlet:renderURL>

	<aui:button href="${editPatcherFixPackURL}" value="edit" />

	<c:if test="${patcherFixPack.patcherBuildId > 0}">
		<portlet:renderURL var="viewPatcherBuildURL">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="view" />
			<portlet:param name="id" value="${patcherFixPack.patcherBuildId}" />
		</portlet:renderURL>

		<aui:button href="${viewPatcherBuildURL}" value="view-build" />
	</c:if>

	<c:if test="${mainPatcherBuild.status == WorkflowConstants.STATUS_BUILD_COMPLETE}">
		<portlet:actionURL var="testPatcherFixPackURL">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="test" />
			<portlet:param name="id" value="${patcherFixPack.patcherBuildId}" />
			<portlet:param name="redirect" value="${viewPatcherFixPackURL}" />
		</portlet:actionURL>

		<aui:button href="${testPatcherFixPackURL}" value="test" />
	</c:if>
</aui:button-row>

<aui:field-wrapper name="fixes-in-fix-pack" />

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-fixes"
	total="${fn:length(patcherFixes)}"
>
	<liferay-ui:search-container-results
		results="${patcherFixes}"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherFix"
		escapedModel="${true}"
		keyProperty="patcherFixId"
		modelVar="patcherFix"
	>
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
			name="name"
		>
			<c:set value="${StringUtil.split(patcherFix.getName())}" var="tokens" />

			<c:forEach items="${tokens}" var="token" varStatus="tokenStatus">
				<a class="nobr" href="${PortletPropsValues.JIRA_URL}/${token}" target="_blank">${token}</a>${(!tokenStatus.last) ? StringPool.COMMA : StringPool.BLANK}
			</c:forEach>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<liferay-ui:search-container-column-text
			href="${PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId())}"
			name="git-hash"
			target="_blank"
			value="${fn:substring(patcherFix.gitHash, 0, 10)}"
		/>

		<liferay-ui:search-container-column-text
			property="dependencies"
		/>

		<liferay-ui:search-container-column-text
			name="status"
			value='<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherFix.getStatus())) + ">" %>'
		/>

		<c:set value="${LanguageUtil.get(request, WorkflowConstantsMethods.getStatusLabel(patcherFix.getFixPackStatus()))}" var="fixPackStatus" />

		<liferay-ui:search-container-column-text
			name="fix-pack-status"
			value="${(patcherFix.fixPackStatus > 0) ? fixPackStatus : StringPool.BLANK}"
		/>

		<liferay-ui:search-container-column-text
			name="type"
			value="<%= LanguageUtil.get(request, PatcherFixConstants.getTypeLabel(patcherFix.getType())) %>"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:if test="${PatcherPermission.contains(themeDisplay, patcherFix, PatcherActionKeys.EDIT, patcherFix.userId) && patcherFix.latestFix}">
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

				<c:if test="${PatcherPermission.contains(themeDisplay, patcherFix, PatcherActionKeys.EDIT_FIX_PACK_FIELDS, patcherFix.userId)}">
					<portlet:renderURL var="editPatcherFixFixPackFieldsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="editFixPackFields" />
						<portlet:param name="id" value="${patcherFix.patcherFixId}" />
					</portlet:renderURL>

					<c:set value='${UnicodeLanguageUtil.get(request, "edit-fix-packs")}' var="editPatcherFixFixPackFieldsURLTitle" />

					<c:set value="javascript:Liferay.Patcher.openWindow('${editPatcherFixFixPackFieldsURL}', '${editPatcherFixFixPackFieldsURLTitle}', true, 800)" var="editPatcherFixFixPackFieldsURL" />

					<liferay-ui:icon
						image="edit"
						message="edit-fix-packs"
						method="get"
						url="${editPatcherFixFixPackFieldsURL}"
					/>
				</c:if>

				<c:if test="${PatcherPermission.contains(themeDisplay, patcherFix, PatcherActionKeys.SET_FIX_PACK_FIELDS, patcherFix.userId)}">
					<portlet:renderURL var="viewPatcherFixPackURL">
						<portlet:param name="controller" value="fix_packs" />
						<portlet:param name="action" value="view" />
						<portlet:param name="id" value="${patcherFixPack.patcherFixPackId}" />
					</portlet:renderURL>

					<portlet:actionURL var="setFixPackFieldsURL">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="setFixPackFields" />
						<portlet:param name="id" value="${patcherFix.patcherFixId}" />
						<portlet:param name="redirect" value="${viewPatcherFixPackURL}" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="remove"
						message="remove-fix-packs"
						url="${setFixPackFieldsURL}"
					/>
				</c:if>

				<c:if test="${false}">
					<portlet:actionURL var="deletePatcherFixURL">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="delete" />
						<portlet:param name="id" value="${patcherFix.patcherFixId}" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon-delete
						url="${deletePatcherFixURL}"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		paginate="${false}"
	/>
</liferay-ui:search-container>

<c:if test="${not empty prerequisitePatcherFixPacks}">
	<aui:field-wrapper name="dependencies" />

	<liferay-ui:search-container
		total="${fn:length(prerequisitePatcherFixPacks)}"
	>
		<liferay-ui:search-container-results
			results="${prerequisitePatcherFixPacks}"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.osb.patcher.model.PatcherFixPack"
			escapedModel="${true}"
			keyProperty="patcherFixPackId"
			modelVar="patcherFixPack"
		>
			<portlet:renderURL var="viewPatcherFixPackURL">
				<portlet:param name="controller" value="fix_packs" />
				<portlet:param name="action" value="view" />
				<portlet:param name="id" value="${patcherFixPack.patcherFixPackId}" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				href="${viewPatcherFixPackURL}"
				name="name"
				value="${patcherFixPack.name}"
			/>

			<c:set value="${PatcherFixComponentLocalServiceUtil.getPatcherFixComponent(patcherFixPack.getPatcherFixComponentId())}" var="patcherFixComponent" />

			<liferay-ui:search-container-column-text
				name="component"
				value="${patcherFixComponent.name}"
			/>

			<liferay-ui:search-container-column-text
				name="version"
				value="${patcherFixPack.version}"
			/>

			<c:set value="${PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(patcherFixPack.getPatcherProjectVersionId())}" var="patcherProjectVersion" />

			<liferay-ui:search-container-column-text
				name="project-version"
				value="${patcherProjectVersion.name}"
			/>

			<liferay-ui:search-container-column-text
				name="status"
				value='<%= LanguageUtil.get(WorkflowConstants.getStatusLabel(patcherFixPack.getStatus())) + ">" %>'
			/>

			<liferay-ui:search-container-column-text
				name="released-date"
			>
				<fmt:formatDate
					value="${patcherFixPack.releasedDate}"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			paginate="${false}"
		/>
	</liferay-ui:search-container>
</c:if>