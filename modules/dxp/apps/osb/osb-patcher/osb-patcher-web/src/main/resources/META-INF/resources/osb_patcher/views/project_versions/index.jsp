<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="project-versions" />
</liferay-util:include>

<aui:layout>
	<aui:column>
		<aui:select label="product-version" name="patcherProductVersionId" onChange="${renderResponse.namespace}productVersionOnChange(this.value);" showEmptyOption="${true}">
			<c:forEach items="${patcherProductVersions}" var="patcherProductVersion">
				<aui:option label="${patcherProductVersion.name}" value="${patcherProductVersion.patcherProductVersionId}" />
			</c:forEach>

			<aui:option label="any" value="0" />
		</aui:select>
	</aui:column>
</aui:layout>

<c:if test="${permissionChecker.isCompanyAdmin()}">
	<aui:button-row>
		<portlet:renderURL var="createPatcherProjectVersionURL">
			<portlet:param name="controller" value="project_versions" />
			<portlet:param name="action" value="create" />
			<portlet:param name="patcherProductVersionId" value="${patcherProductVersionId}" />
		</portlet:renderURL>

		<aui:button href="${createPatcherProjectVersionURL}" value="create-project-version" />
	</aui:button-row>
</c:if>

<portlet:renderURL var="viewPatcherProjectVersionsURL">
	<portlet:param name="controller" value="project_versions" />
	<portlet:param name="action" value="index" />
</portlet:renderURL>

<aui:form action="${viewPatcherProjectVersionsURL}" method="get" name="fm">
	<aui:input name="patcherProductVersionId" type="hidden" value="${patcherProductVersionId}" />

	<aui:fieldset>
		<aui:input inlineField="${true}" label="" name="keywords" size="30" title="search-project-versions" type="text" />

		<aui:button type="submit" value="search" />
	</aui:fieldset>
</aui:form>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-project-versions"
	iteratorURL="${alloySearchResult.portletURL}"
>
	<liferay-ui:search-container-results
		results="${alloySearchResult.baseModels}"
		total="${alloySearchResult.size}"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherProjectVersion"
		escapedModel="${true}"
		keyProperty="patcherProjectVersionId"
		modelVar="patcherProjectVersion"
	>
		<liferay-ui:search-container-column-text
			name="product-version"
			value="${PatcherProductVersionUtil.fetchPatcherProductVersionName(patcherProjectVersion.getPatcherProductVersionId())}"
		/>

		<liferay-ui:search-container-column-text
			property="name"
		/>

		<c:if test="${permissionChecker.isCompanyAdmin()}">
			<liferay-ui:search-container-column-text
				name="combined-branch"
				property="combinedBranch"
			/>
		</c:if>

		<liferay-ui:search-container-column-text
			name="tag-name"
			property="committish"
		/>

		<liferay-ui:search-container-column-text
			name="repository-name"
			property="repositoryName"
		/>

		<liferay-ui:search-container-column-text
			name="root-project-version"
			value="${PatcherProjectVersionUtil.getRootPatcherProjectVersionName(patcherProjectVersion)}"
		/>

		<liferay-ui:search-container-column-text
			name="fixed-issues"
		>
			<portlet:renderURL var="viewPatcherProjectVersionFixedIssuesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
				<portlet:param name="controller" value="project_versions" />
				<portlet:param name="action" value="fixedIssues" />
				<portlet:param name="id" value="${patcherProjectVersion.patcherProjectVersionId}" />
			</portlet:renderURL>

			<c:set value='${AlloyLanguageUtil.formatUnicode("fixed-issues")}' var="viewPatcherProjectVersionFixedIssuesURLTitle" />

			<c:set value="javascript:Liferay.Patcher.openWindow('${viewPatcherProjectVersionFixedIssuesURL}', '${viewPatcherProjectVersionFixedIssuesURLTitle}', true, 1000, 1);" var="viewPatcherProjectVersionFixedIssuesURL" />

			<c:set value="${PatcherUtil.getTicketsCount(patcherProjectVersion.fixedIssues)}" var="ticketsCount" />

			<c:set value='${ticketsCount} ${AlloyLanguageUtil.format("tickets")}' var="tickets" />

			<a class="nobr" href="${viewPatcherProjectVersionFixedIssuesURL}" title="${patcherProjectVersionFixedIssuesCount}">${ticketsCount > 0 ? tickets : ""} </a>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:if test='${PatcherPermission.contains(themeDisplay, patcherProjectVersion, "edit")}'>
					<portlet:renderURL var="editPatcherProjectVersionURL">
						<portlet:param name="controller" value="project_versions" />
						<portlet:param name="action" value="edit" />
						<portlet:param name="id" value="${patcherProjectVersion.patcherProjectVersionId}" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="${editPatcherProjectVersionURL}"
					/>
				</c:if>

				<c:if test='${PatcherPermission.contains(themeDisplay, patcherProjectVersion, "delete")}'>
					<portlet:actionURL var="deletePatcherProjectVersionURL">
						<portlet:param name="controller" value="project_versions" />
						<portlet:param name="action" value="delete" />
						<portlet:param name="id" value="${patcherProjectVersion.patcherProjectVersionId}" />
						<portlet:param name="redirect" value="${alloySearchResult.portletURL}" />
					</portlet:actionURL>

					<liferay-ui:icon-delete
						url="${deletePatcherProjectVersionURL}"
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

			window.location.href = Liferay.Patcher.updateProductVersionId('${viewPatcherProjectVersionsURL}', productVersionId, namespace);
		},
		['aui-base']
	);
</aui:script>