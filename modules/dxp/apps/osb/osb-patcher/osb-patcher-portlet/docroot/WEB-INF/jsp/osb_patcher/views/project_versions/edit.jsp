<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/WEB-INF/jsp/osb_patcher/views/init.jsp" %>

<liferay-util:include page="/WEB-INF/jsp/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="project-versions" />
</liferay-util:include>

<liferay-util:include page="/WEB-INF/jsp/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="${patcherProjectVersion.name}" />
	<liferay-util:param name="controller" value="project_versions" />
	<liferay-util:param name="action" value="index" />
</liferay-util:include>

<aui:model-context bean="${patcherProjectVersion}" model="<%= PatcherProjectVersion.class %>" />

<portlet:actionURL var="updatePatcherProjectVersionURL">
	<portlet:param name="controller" value="project_versions" />
	<portlet:param name="action" value="update" />
</portlet:actionURL>

<aui:form action="${updatePatcherProjectVersionURL}" method="post">
	<portlet:renderURL var="viewPatcherProjectVersionsURL">
		<portlet:param name="controller" value="project_versions" />
		<portlet:param name="action" value="index" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="${viewPatcherProjectVersionsURL}" />
	<aui:input name="id" type="hidden" value="${patcherProjectVersion.patcherProjectVersionId}" />

	<aui:select label="product-version" name="patcherProductVersionId" onChange="${renderResponse.namespace}toggleFixedIssuesField();${renderResponse.namespace}toggleHideCheckbox();">
		<c:forEach items="${patcherProductVersions}" var="patcherProductVersion">
			<aui:option label="${patcherProductVersion.getName()}" value="${patcherProductVersion.getPatcherProductVersionId()}" />
		</c:forEach>
	</aui:select>

	<aui:input disabled="${patcherProjectVersion.patcherProductVersionId != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)}" name="name" />

	<c:if test="${permissionChecker.isCompanyAdmin()}">
		<aui:input name="combinedBranch" onChange="${renderResponse.namespace}toggleHideCheckbox()" type="checkbox" />
	</c:if>

	<aui:input label="tag-name" name="committish" />

	<aui:input name="repositoryName" />

	<span class="hide" id="${renderResponse.namespace}displayingFixedIssues">
		<aui:input name="fixedIssues" />
	</span>

	<c:if test="${permissionChecker.isCompanyAdmin()}">
		<span class="hide" id="${renderResponse.namespace}displayingHide">
			<aui:input name="hide" />
		</span>
	</c:if>

	<aui:button-row>
		<aui:button type="submit" value="update" />

		<aui:button href="${viewPatcherProjectVersionsURL}" value="cancel" />
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

		var dxp70AndNewerPatcherProductVersionIds = ${dxp70AndNewerPatcherProductVersionIdsJSONArray};

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

		var combinedBranchSelected = A.one('#<portlet:namespace />combinedBranchCheckbox').attr('checked');

		var marketplaceReleasePatcherProductVersionIds = ${marketplaceReleasePatcherProductVersionIds};

		if (marketplaceReleasePatcherProductVersionIds.includes(parseInt(productVersionId)) && !combinedBranchSelected) {
			A.one('#<portlet:namespace />displayingHide').show();
		}
		else {
			A.one('#<portlet:namespace />displayingHide').hide();
			A.one('#<portlet:namespace />hideCheckbox').attr('checked', false);
		}
	}
</aui:script>