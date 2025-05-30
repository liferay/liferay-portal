<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherProjectVersionsDisplayContext patcherProjectVersionsDisplayContext = new PatcherProjectVersionsDisplayContext(request, renderRequest, renderResponse);
%>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="project-versions" />
	<liferay-util:param name="patcherProductVersionId" value="<%= String.valueOf(patcherProjectVersionsDisplayContext.getPatcherProductVersionId()) %>" />
</liferay-util:include>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="create-project-version" />
	<liferay-util:param name="mvcRenderCommandName" value="/patcher/index_project_versions" />
	<liferay-util:param name="patcherProductVersionId" value="<%= String.valueOf(patcherProjectVersionsDisplayContext.getPatcherProductVersionId()) %>" />
</liferay-util:include>

<aui:model-context bean="<%= null %>" model="<%= PatcherProjectVersion.class %>" />

<portlet:actionURL name="/patcher/add_project_versions" var="addPatcherProjectVersionURL" />

<aui:form action="<%= addPatcherProjectVersionURL %>" method="post">
	<portlet:renderURL var="viewPatcherProjectVersionsURL">
		<portlet:param name="mvcRenderCommandName" value="/patcher/index_project_versions" />
		<portlet:param name="patcherProductVersionId" value="<%= String.valueOf(patcherProjectVersionsDisplayContext.getPatcherProductVersionId()) %>" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="<%= viewPatcherProjectVersionsURL %>" />

	<aui:select label="product-version" name="patcherProductVersionId" onChange='<%= liferayPortletResponse.getNamespace() + "toggleFixedIssuesField();" + liferayPortletResponse.getNamespace() + "toggleHideCheckbox();" %>'>

		<%
		for (PatcherProductVersion patcherProductVersion : PatcherProductVersionUtil.getPatcherProductVersions()) {
		%>

			<aui:option label="<%= patcherProductVersion.getName() %>" value="<%= patcherProductVersion.getPatcherProductVersionId() %>" />

		<%
		}
		%>

	</aui:select>

	<aui:input name="name" />

	<c:if test="<%= permissionChecker.isCompanyAdmin() %>">
		<aui:input name="combinedBranch" onChange='<%= liferayPortletResponse.getNamespace() + "toggleHideCheckbox()" %>' type="checkbox" />
	</c:if>

	<aui:input label="tag-name" name="committish" />

	<aui:input name="repositoryName" />

	<span class="hide" id="<portlet:namespace />displayingFixedIssues">
		<aui:input name="fixedIssues" />
	</span>

	<aui:select label="project-version-root" name="rootPatcherProjectVersionId" showEmptyOption="<%= true %>">

		<%
		for (PatcherProjectVersion patcherProjectVersion : PatcherProjectVersionLocalServiceUtil.getRootPatcherProjectVersions()) {
		%>

			<aui:option label="<%= patcherProjectVersion.getName() %>" value="<%= patcherProjectVersion.getPatcherProjectVersionId() %>" />

		<%
		}
		%>

	</aui:select>

	<c:if test="<%= permissionChecker.isCompanyAdmin() %>">
		<span class="hide" id="<portlet:namespace />displayingHide">
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

		var dxp70AndNewerPatcherProductVersionIds = <%= patcherProjectVersionsDisplayContext.getDXP70AndNewerPatcherProductVersionIdsJSONArray() %>;

		var patcherProductVersionId = A.one('#<portlet:namespace />patcherProductVersionId').val();

		if (dxp70AndNewerPatcherProductVersionIds.indexOf(patcherProductVersionId) < 0) {
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

		var marketplaceReleasePatcherProductVersionIds = <%= patcherProjectVersionsDisplayContext.getMarketplaceReleasePatcherProductVersionIdsJSONArray() %>;

		if (marketplaceReleasePatcherProductVersionIds.includes(productVersionId) && !combined) {
			A.one('#<portlet:namespace />displayingHide').show();
		}
		else {
			A.one('#<portlet:namespace />displayingHide').hide();
			A.one('#<portlet:namespace />hide').attr('checked', false);
		}
	}
</aui:script>