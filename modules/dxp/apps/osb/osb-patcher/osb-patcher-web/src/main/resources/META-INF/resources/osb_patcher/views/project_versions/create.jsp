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

<liferay-ui:header
	title="create-project-version"
/>

<aui:model-context bean="<%= null %>" model="<%= PatcherProjectVersion.class %>" />

<portlet:actionURL name="/patcher/add_project_versions" var="addPatcherProjectVersionURL" />

<liferay-frontend:edit-form
	action="<%= addPatcherProjectVersionURL %>"
	fluid="<%= true %>"
	method="post"
	name="fm"
>
	<liferay-frontend:edit-form-body>
		<aui:input name="redirect" type="hidden" value="<%= patcherProjectVersionsDisplayContext.getRedirect() %>" />

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
			<aui:input name="fixedIssues" type="textarea" />
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
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= patcherProjectVersionsDisplayContext.getRedirect() %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script>
	AUI().ready(function () {
		<portlet:namespace />toggleFixedIssuesField();
		<portlet:namespace />toggleHideCheckbox();
	});

	function <portlet:namespace />toggleFixedIssuesField() {
		var A = AUI();

		var dxp70AndNewerPatcherProductVersionIds =
			<%= patcherProjectVersionsDisplayContext.getDXP70AndNewerPatcherProductVersionIdsJSONArray() %>;

		var patcherProductVersionId = A.one(
			'#<portlet:namespace />patcherProductVersionId'
		).val();

		if (
			dxp70AndNewerPatcherProductVersionIds.indexOf(patcherProductVersionId) <
			0
		) {
			A.one('#<portlet:namespace />displayingFixedIssues').hide();
			A.one('#<portlet:namespace />fixedIssues').val('');
		}
		else {
			A.one('#<portlet:namespace />displayingFixedIssues').show();
		}
	}

	function <portlet:namespace />toggleHideCheckbox() {
		var A = AUI();

		var productVersionId = A.one(
			'#<portlet:namespace />patcherProductVersionId'
		).val();

		var combined = A.one('#<portlet:namespace />combinedBranch').attr(
			'checked'
		);

		var marketplaceReleasePatcherProductVersionIds =
			<%= patcherProjectVersionsDisplayContext.getMarketplaceReleasePatcherProductVersionIdsJSONArray() %>;

		if (
			marketplaceReleasePatcherProductVersionIds.includes(productVersionId) &&
			!combined
		) {
			A.one('#<portlet:namespace />displayingHide').show();
		}
		else {
			A.one('#<portlet:namespace />displayingHide').hide();
			A.one('#<portlet:namespace />hide').attr('checked', false);
		}
	}
</aui:script>