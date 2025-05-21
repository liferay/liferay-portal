<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="fixes" />
	</liferay-util:include>

	<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
		<liferay-util:param name="title" value="view-fix" />
		<liferay-util:param name="controller" value="fixes" />
		<liferay-util:param name="action" value="index" />
	</liferay-util:include>
</c:if>

<aui:model-context bean="${patcherFix}" model="<%= PatcherFix.class %>" />

<portlet:renderURL var="viewPatcherFixURL">
	<portlet:param name="controller" value="fixes" />
	<portlet:param name="action" value="view" />
	<portlet:param name="id" value="${patcherFix.patcherFixId}" />

	<c:if test="${not empty redirect}">
		<portlet:param name="redirect" value="${redirect}" />
	</c:if>
</portlet:renderURL>

<c:if test="${!patcherFix.latestFix}">
	<liferay-ui:message key="this-is-not-the-latest-fix-version-view-the-latest-fix-here" />

	<portlet:renderURL var="viewLatestPatcherFixURL">
		<portlet:param name="controller" value="fixes" />
		<portlet:param name="action" value="view" />
		<portlet:param name="id" value="${latestPatcherFix.patcherFixId}" />
		<portlet:param name="redirect" value="${viewPatcherFixURL}" />
	</portlet:renderURL>

	<aui:a href="${viewLatestPatcherFixURL}">
		${latestPatcherFix.patcherFixId}
	</aui:a>
</c:if>

<aui:field-wrapper name="modifiedDate">
	<fmt:formatDate
		type="both"
		value="${patcherFix.modifiedDate}"
	/>
</aui:field-wrapper>

<aui:field-wrapper name="statusDate">
	<fmt:formatDate
		type="both"
		value="${patcherFix.statusDate}"
	/>
</aui:field-wrapper>

<aui:field-wrapper name="createdBy">
	${patcherFix.userName}
</aui:field-wrapper>

<aui:field-wrapper name="statusUpdatedBy">
	${patcherFix.statusByUserName}
</aui:field-wrapper>

<aui:field-wrapper name="fixId">
	${patcherFix.patcherFixId}
</aui:field-wrapper>

<aui:field-wrapper name="version">
	${patcherFix.keyVersion}
</aui:field-wrapper>

<aui:field-wrapper name="patcher-status">
	${patcherFixStatus}
</aui:field-wrapper>

<aui:select disabled="${true}" label="product-version" name="patcherProductVersionId" showEmptyOption="${true}">
	<c:forEach items="${patcherProductVersions}" var="patcherProductVersion">
		<aui:option label="${patcherProductVersion.getName()}" value="${patcherProductVersion.getPatcherProductVersionId()}" />
	</c:forEach>
</aui:select>

<aui:select disabled="${true}" label="project-version" name="patcherProjectVersionId" showEmptyOption="${false}">
	<c:forEach items="${patcherProjectVersions}" var="patcherProjectVersion">
		<aui:option label="${patcherProjectVersion.name}" value="${patcherProjectVersion.patcherProjectVersionId}" />
	</c:forEach>
</aui:select>

<aui:field-wrapper name="git-hash">
	<a href="${gitHubURL}" target="_blank">${patcherFix.gitHash}</a>
</aui:field-wrapper>

<aui:field-wrapper name="jenkins">
	<c:forEach items="${jenkinsResults}" var="jenkinsResult">
		<aui:a cssClass="nobr" href="${jenkinsResult.statusURL}" label="${jenkinsResult.jobName}" target="_blank" />
	</c:forEach>
</aui:field-wrapper>

<aui:input inputCssClass="osb-patcher-input-wide osb-patcher-read-only" label="content" name="patcherFixName" readonly="${true}" type="textarea" value="${patcherFix.name}" />

<aui:input inputCssClass="osb-patcher-read-only" label="branch-name" name="committish" readonly="${true}" type="text" />

<aui:input inputCssClass="osb-patcher-read-only" label="github-url" name="gitRemoteURL" readonly="${true}" type="text" />

<aui:select disabled="${true}" name="type" showEmptyOption="${false}">
	<aui:option label="${PatcherFixConstants.LABEL_EXCLUDED}" value="${PatcherFixConstants.TYPE_EXCLUDED}" />
	<aui:option label="${PatcherFixConstants.LABEL_GENERATED}" value="${PatcherFixConstants.TYPE_GENERATED}" />
	<aui:option label="${PatcherFixConstants.LABEL_GENERATED_PRIVATE_PUBLIC}" value="${PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC}" />
	<aui:option label="${PatcherFixConstants.LABEL_PATCH}" value="${PatcherFixConstants.TYPE_PATCH}" />
	<aui:option label="${PatcherFixConstants.LABEL_REBASE}" value="${PatcherFixConstants.TYPE_REBASE}" />
	<aui:option label="${PatcherFixConstants.LABEL_WORKAROUND}" value="${PatcherFixConstants.TYPE_WORKAROUND}" />
</aui:select>

<aui:button-row>
	<c:if test="${patcherFix.latestFix}">
		<portlet:renderURL var="editPatcherFixURL">
			<portlet:param name="controller" value="fixes" />
			<portlet:param name="action" value="edit" />
			<portlet:param name="id" value="${patcherFix.patcherFixId}" />
			<portlet:param name="redirect" value="${viewPatcherFixURL}" />
		</portlet:renderURL>

		<aui:button href="${editPatcherFixURL}" value="edit" />
	</c:if>

	<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
		<portlet:renderURL var="viewPatcherBuildsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="fixes" />
			<portlet:param name="action" value="builds" />
			<portlet:param name="id" value="${patcherFix.patcherFixId}" />
		</portlet:renderURL>

		<c:set value='${AlloyLanguageUtil.formatUnicode("view-builds-for-fix-id-x", patcherFix.patcherFixId)}' var="viewPatcherBuildsURLTitle" />

		<c:set value="javascript:Liferay.Patcher.openWindow('${viewPatcherBuildsURL}', '${viewPatcherBuildsURLTitle}', true, 1000);" var="viewPatcherBuildsURL" />

		<aui:button onClick="${viewPatcherBuildsURL}" value="view-builds" />

		<portlet:renderURL var="viewPatcherFixesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="fixes" />
			<portlet:param name="action" value="fixes" />
			<portlet:param name="id" value="${patcherFix.patcherFixId}" />
		</portlet:renderURL>

		<c:set value='${AlloyLanguageUtil.formatUnicode("view-fixes-for-fix-id-x", patcherFix.patcherFixId)}' var="viewPatcherFixesURLTitle" />

		<c:set value="javascript:Liferay.Patcher.openWindow('${viewPatcherFixesURL}', '${viewPatcherFixesURLTitle}', true, 1000);" var="viewPatcherFixesURL" />

		<aui:button onClick="${viewPatcherFixesURL}" value="view-fixes" />

		<c:if test='${PatcherPermission.contains(themeDisplay, patcherFix, "editFixPackFields") && (patcherFix.patcherFixId > 0)}'>
			<portlet:renderURL var="editPatcherFixFixPackFieldsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
				<portlet:param name="controller" value="fixes" />
				<portlet:param name="action" value="editFixPackFields" />
				<portlet:param name="id" value="${patcherFix.patcherFixId}" />
			</portlet:renderURL>

			<c:set value='${AlloyLanguageUtil.formatUnicode("edit-fix-packs")}' var="editPatcherFixFixPackFieldsURLTitle" />

			<c:set value="javascript:Liferay.Patcher.openWindow('${editPatcherFixFixPackFieldsURL}', '${editPatcherFixFixPackFieldsURLTitle}', true, 800)" var="editPatcherFixFixPackFieldsURL" />

			<aui:button onClick="${editPatcherFixFixPackFieldsURL}" value="edit-fix-packs" />
		</c:if>
	</c:if>
</aui:button-row>

<c:if test="${alloySearchResult.size > 1}">
	<aui:field-wrapper name="fix-versions" />

	<liferay-ui:search-container>
		<liferay-ui:search-container-results
			results="${alloySearchResult.baseModels}"
			total="${alloySearchResult.size}"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.osb.patcher.model.PatcherFix"
			escapedModel="${true}"
			keyProperty="patcherFixId"
			modelVar="patcherFixKeyVersion"
		>
			<c:if test="${patcherFix.patcherFixId == patcherFixKeyVersion.patcherFixId}">
				<liferay-ui:search-container-row-parameter
					name="className"
					value="selected"
				/>
			</c:if>

			<portlet:renderURL var="viewPatcherFixKeyVersionURL">
				<portlet:param name="controller" value="fixes" />
				<portlet:param name="action" value="view" />
				<portlet:param name="id" value="${patcherFixKeyVersion.patcherFixId}" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				href="${(patcherFix.patcherFixId != patcherFixKeyVersion.patcherFixId) ? viewPatcherFixKeyVersionURL : null}"
				name="fix-id"
				property="patcherFixId"
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
					url="${PatcherUtil.getUserDisplayURL(themeDisplay, patcherFixKeyVersion.getUserId())}"
					userId="${patcherFixKeyVersion.userId}"
					userName="${patcherFixKeyVersion.userName}"
				/>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="modified-date"
			>
				<fmt:formatDate
					type="both"
					value="${patcherFixKeyVersion.modifiedDate}"
				/>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				href="${PatcherFixUtil.getPatcherFixGitHubURL(patcherFixKeyVersion.getPatcherFixId())}"
				name="git-hash"
				target="_blank"
				value="${fn:substring(patcherFixKeyVersion.gitHash, 0, 10)}"
			/>

			<liferay-ui:search-container-column-text
				name="patcher-status"
				value="${AlloyLanguageUtil.format(WorkflowConstantsMethods.getStatusLabel(patcherFixKeyVersion.getStatus()))}"
			/>

			<liferay-ui:search-container-column-text
				name="type"
				value="${AlloyLanguageUtil.format(PatcherFixConstantsMethods.getTypeLabel(patcherFixKeyVersion.getType()))}"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			paginate="${false}"
		/>
	</liferay-ui:search-container>
</c:if>

<aui:script>
	YUI().ready(
		'aui-popover',
		function(Y) {
			var align_points = [Y.WidgetPositionAlign.LC, Y.WidgetPositionAlign.RC];
			var tickets = document.getElementById('_1_WAR_osbpatcherportlet_patcherFixName');
			var trigger = Y.one('#_1_WAR_osbpatcherportlet_patcherFixName');

			Liferay.Patcher.getTicketLinksPopover(Y, align_points, tickets, trigger)
		}
	);
</aui:script>