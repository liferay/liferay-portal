<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

long patcherFixId = ParamUtil.getLong(request, "patcherFixId");

PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(patcherFixId);
%>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="fixes" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="edit-fix" />
	<liferay-util:param name="mvcRenderCommandName" value="/patcher/index_fixes" />
</liferay-util:include>

<aui:model-context bean="<%= patcherFix %>" model="<%= PatcherFix.class %>" />

<portlet:actionURL name="/patcher/update_fixes" var="updatePatcherFixURL" />

<aui:form action="<%= updatePatcherFixURL %>" method="post" name="fm">
	<portlet:renderURL var="viewPatcherFixesURL">
		<portlet:param name="mvcRenderCommandName" value="/patcher/index_fixes" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="<%= viewPatcherFixesURL %>" />
	<aui:input name="patcherFixId" type="hidden" value="<%= patcherFix.getPatcherFixId() %>" />

	<aui:field-wrapper label="modified-date">
		<fmt:formatDate
			type="both"
			value="<%= patcherFix.getModifiedDate() %>"
		/>
	</aui:field-wrapper>

	<aui:field-wrapper label="created-by">
		<%= patcherFix.getUserName() %>
	</aui:field-wrapper>

	<aui:field-wrapper label="fix-id">
		<%= patcherFix.getPatcherFixId() %>
	</aui:field-wrapper>

	<aui:field-wrapper label="version">
		<%= patcherFix.getKeyVersion() %>
	</aui:field-wrapper>

	<aui:field-wrapper label="patcher-status">
		<liferay-ui:message key="<%= WorkflowConstants.getStatusLabel(patcherFix.getStatus()) %>" />
	</aui:field-wrapper>

	<aui:select disabled="<%= true %>" label="product-version" name="patcherProductVersionId" required="<%= true %>">

		<%
		for (PatcherProductVersion patcherProductVersion : PatcherProductVersionUtil.getPatcherProductVersions()) {
		%>

			<aui:option label="<%= patcherProductVersion.getName() %>" value="<%= patcherProductVersion.getPatcherProductVersionId() %>" />

		<%
		}
		%>

	</aui:select>

	<aui:select disabled="<%= true %>" label="project-version" name="patcherProjectVersionId" required="<%= true %>" showEmptyOption="<%= true %>">

		<%
		for (PatcherProjectVersion patcherProjectVersion : PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions(patcherFix.getPatcherProductVersionId())) {
		%>

			<aui:option label="<%= patcherProjectVersion.getName() %>" value="<%= patcherProjectVersion.getPatcherProjectVersionId() %>" />

		<%
		}
		%>

	</aui:select>

	<aui:field-wrapper label="git-hash">
		<a href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()) %>" target="_blank"><%= patcherFix.getGitHash() %></a>
	</aui:field-wrapper>

	<aui:input inputCssClass="osb-patcher-input-wide osb-patcher-read-only" label="content" name="patcherFixName" readonly="<%= true %>" type="textarea" value="<%= patcherFix.getName() %>" />

	<aui:input label="branch-name" name="committish" />

	<aui:input label="github-url" name="gitRemoteURL" />

	<aui:input name="workaround" type="checkbox" value="<%= patcherFix.getType() == PatcherFixConstants.TYPE_WORKAROUND %>" />

	<aui:button-row>
		<aui:button type="submit" value="update" />

		<aui:button href="<%= Validator.isNotNull(redirect) ? redirect : viewPatcherFixesURL %>" value="cancel" />

		<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT_FIX_PACK_FIELDS, patcherFix.getUserId()) %>">
			<clay:button
				displayType="secondary"
				label='<%= LanguageUtil.get(request, "edit-fix-packs") %>'
				onClick='<%= liferayPortletResponse.getNamespace() + "handleClick()" %>'
			/>
		</c:if>
	</aui:button-row>
</aui:form>

<aui:script>
	function <portlet:namespace />handleClick() {
		Liferay.Util.openModal({
			title: '<liferay-ui:message key="edit-fix-packs" />',
			url: '<portlet:renderURL windowState="<%= LiferayWindowState.POP_UP.toString() %>"><portlet:param name="mvcRenderCommandName" value="/patcher/edit_fix_pack_fields_fixes" /><portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" /></portlet:renderURL>',
		});
	}
</aui:script>