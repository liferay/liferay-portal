<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="project-versions" />
	<liferay-util:param name="patcherProductVersionId" value="<%= patcherProductVersionId %>" />
</liferay-util:include>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="create-project-version" />
	<liferay-util:param name="mvcRenderCommandName" value="/patcher/index_project_versions" />
	<liferay-util:param name="patcherProductVersionId" value="<%= patcherProductVersionId %>" />
</liferay-util:include>

<aui:model-context bean="<%= patcherProjectVersion %>" model="<%= PatcherProjectVersion.class %>" />

<portlet:actionURL var="addPatcherProjectVersionURL">
	<portlet:param name="controller" value="project_versions" />
	<portlet:param name="action" value="add" />
</portlet:actionURL>

<aui:form action="<%= addPatcherProjectVersionURL %>" method="post">
	<portlet:renderURL var="viewPatcherProjectVersionsURL">
		<portlet:param name="mvcRenderCommandName" value="/patcher/index_project_versions" />
		<portlet:param name="patcherProductVersionId" value="<%= patcherProductVersionId %>" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="<%= viewPatcherProjectVersionsURL %>" />

	<aui:select label="product-version" name="patcherProductVersionId" onChange="<%= renderResponse.getNamespace() %>toggleFixedIssuesField();<%= renderResponse.getNamespace() %>toggleHideCheckbox()">
		<c:forEach items="<%= patcherProductVersions %>" var="patcherProductVersion">
			<aui:option label="<%= patcherProductVersion.getName() %>" value="<%= patcherProductVersion.getPatcherProductVersionId() %>" />
		</c:forEach>
	</aui:select>

	<aui:input name="name" />

	<c:if test="<%= permissionChecker.isCompanyAdmin() %>">
		<aui:input name="combinedBranch" onChange="<%= renderResponse.getNamespace() %>toggleHideCheckbox()" type="checkbox" />
	</c:if>

	<aui:input label="tag-name" name="committish" />

	<aui:input name="repositoryName" />

	<span class="hide" id="<%= renderResponse.getNamespace() %>displayingFixedIssues">
		<aui:input name="fixedIssues" />
	</span>

	<aui:select disabled="<%= disabled %>" label="project-version-root" name="rootPatcherProjectVersionId" showEmptyOption="<%= true %>">
		<c:forEach items="<%= rootPatcherProjectVersions %>" var="curRootPatcherProjectVersion">
			<aui:option label="<%= curRootPatcherProjectVersion.name %>" value="<%= curRootPatcherProjectVersion.getPatcherProjectVersionId() %>" />
		</c:forEach>
	</aui:select>

	<c:if test="<%= permissionChecker.isCompanyAdmin() %>">
		<span class="hide" id="<%= renderResponse.getNamespace() %>displayingHide">
			<aui:input name="hide" />
		</span>
	</c:if>

	<aui:button-row>
		<aui:button type="submit" value="add" />

		<aui:button href="<%= viewPatcherProjectVersionsURL %>" value="cancel" />
	</aui:button-row>
</aui:form>

<aui:script>
	AUI().ready(
		function() {
			<portlet:namespace />toggleFixedIssuesField();
			<portlet:namespace />toggleHideCheckbox();
		}
	);

	function <portlet:namespace />toggleFixedIssuesField() {
		var A = AUI();

		var dxp70AndNewerPatcherProductVersionIds = <%= dxp70AndNewerPatcherProductVersionIdsJSONArray %>;

		var patcherProductVersionId = A.one('#<portlet:namespace />patcherProductVersionId').val();

		if (dxp70AndNewerPatcherProductVersionIds.indexOf(Number(patcherProductVersionId)) < 0) {
			A.one('#<portlet:namespace />displayingFixedIssues').hide();
			A.one('#<portlet:namespace />fixedIssues').val('');
		}
		else {
			A.one('#<portlet:namespace />displayingFixedIssues').show();
		}
	}

	function <portlet:namespace />toggleHideCheckbox() {
		var A = AUI();

		var productVersionId = A.one('#<portlet:namespace />patcherProductVersionId').val();

		var combined = A.one('#<portlet:namespace />combinedBranch').attr('checked');

		var marketplaceReleasePatcherProductVersionIds = <%= marketplaceReleasePatcherProductVersionIds %>;

		if (marketplaceReleasePatcherProductVersionIds.includes(parseInt(productVersionId)) && !combined) {
			A.one('#<portlet:namespace />displayingHide').show();
		}
		else {
			A.one('#<portlet:namespace />displayingHide').hide();
			A.one('#<portlet:namespace />hide').attr('checked', false);
		}
	}
</aui:script>