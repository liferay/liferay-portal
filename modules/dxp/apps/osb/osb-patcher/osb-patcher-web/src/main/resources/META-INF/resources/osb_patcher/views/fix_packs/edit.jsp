<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");
long patcherFixPackId = ParamUtil.getLong(request, "patcherFixPackId");

PatcherFix patcherFix = null;
String gitHubURL = StringPool.BLANK;
Map<String, String> jenkinsRequestParameters = null;

PatcherFixPack patcherFixPack = PatcherFixPackLocalServiceUtil.fetchPatcherFixPack(patcherFixPackId);

PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherFixPack.getPatcherBuildId());

if (patcherBuild != null) {
	patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(patcherBuild.getPatcherFixId());

	gitHubURL = PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId());

	jenkinsRequestParameters = JenkinsUtil.getDistJenkinsRequestParameters(patcherBuild);

	if (patcherBuild.getPatcherProductVersionId() != PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X)) {
		jenkinsRequestParameters.put("git.revision", patcherFix.getGitHash());
	}
}
%>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="fix-packs" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="edit-fix-pack" />
	<liferay-util:param name="mvcRenderCommandName" value="/patcher/index_fix_packs" />
</liferay-util:include>

<aui:model-context bean="<%= patcherFixPack %>" model="<%= PatcherFixPack.class %>" />

<portlet:actionURL name="/patcher/update_fix_packs" var="updatePatcherFixPackURL" />

<aui:form action="<%= updatePatcherFixPackURL %>" method="post">
	<portlet:renderURL var="viewPatcherFixPacksURL">
		<portlet:param name="mvcRenderCommandName" value="/patcher/index_fix_packs" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="<%= viewPatcherFixPacksURL %>" />
	<aui:input name="patcherFixPackId" type="hidden" value="<%= patcherFixPack.getPatcherFixPackId() %>" />

	<c:set value="<%= patcherFixPack.getPatcherFixPackId() > 0 %>" var="disabled" />

	<c:if test="<%= patcherFix != null %>">
		<aui:field-wrapper label="git-hash">
			<a href="<%= gitHubURL %>" target="_blank"><%= patcherFix.getGitHash() %></a>
		</aui:field-wrapper>
	</c:if>

	<aui:select disabled="<%= true %>" label="project-version" name="patcherProjectVersionId" required="<%= true %>" showEmptyOption="<%= true %>">

		<%
		for (PatcherProjectVersion patcherProjectVersion : PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions()) {
		%>

			<aui:option label="<%= patcherProjectVersion.getName() %>" value="<%= patcherProjectVersion.getPatcherProjectVersionId() %>" />

		<%
		}
		%>

	</aui:select>

	<aui:select disabled="<%= true %>" label="component" name="patcherFixComponentId" required="<%= true %>" showEmptyOption="<%= true %>">

		<%
		for (PatcherFixComponent patcherFixComponent : PatcherFixComponentLocalServiceUtil.getPatcherFixComponents()) {
		%>

			<aui:option label="<%= patcherFixComponent.getName() %>" value="<%= patcherFixComponent.getPatcherFixComponentId() %>" />

		<%
		}
		%>

	</aui:select>

	<aui:input name="version" type="hidden" value="<%= patcherFixPack.getVersion() %>" />

	<aui:field-wrapper label="version">
		<%= patcherFixPack.getVersion() %>
	</aui:field-wrapper>

	<%
	boolean released = patcherFixPack.getStatus() == WorkflowConstants.STATUS_FIX_PACK_RELEASED;
	%>

	<aui:select disabled="<%= released %>" name="status" showEmptyOption="<%= false %>">
		<c:if test="<%= patcherFixPack.getStatus() != WorkflowConstants.STATUS_FIX_PACK_RELEASED %>">
			<aui:option label="<%= WorkflowConstants.LABEL_FIX_PACK_UNDER_DEVELOPMENT %>" value="<%= WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_FIX_PACK_FROZEN %>" value="<%= WorkflowConstants.STATUS_FIX_PACK_FROZEN %>" />
		</c:if>

		<c:if test="<%= patcherFixPack.getStatus() != WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT %>">
			<aui:option label="<%= WorkflowConstants.LABEL_FIX_PACK_RELEASED %>" value="<%= WorkflowConstants.STATUS_FIX_PACK_RELEASED %>" />
		</c:if>
	</aui:select>

	<c:if test="<%= patcherFixPack.getReleasedDate() != null %>">
		<aui:field-wrapper label="released-date">
			<fmt:formatDate
				value="<%= patcherFixPack.getReleasedDate() %>"
			/>
		</aui:field-wrapper>
	</c:if>

	<aui:input disabled="<%= released %>" name="requirements" />

	<aui:button-row>
		<aui:button disabled="<%= released %>" type="submit" value="update" />

		<aui:button href="<%= Validator.isNotNull(redirect) ? redirect : viewPatcherFixPacksURL %>" value="cancel" />

		<c:if test="<%= (patcherFix != null) && Validator.isNotNull(patcherFix.getGitHash()) %>">
			<portlet:actionURL name="/patcher/build_fix_packs" var="buildPatcherBuildURL">
				<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />
				<portlet:param name="redirect" value="<%= viewPatcherFixPacksURL %>" />
			</portlet:actionURL>

			<aui:button href="<%= buildPatcherBuildURL %>" value="build" />
		</c:if>

		<c:if test="<%= patcherFixPack.getPatcherBuildId() > 0 %>">
			<portlet:renderURL var="viewPatcherBuildURL">
				<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds" />
				<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherFixPack.getPatcherBuildId()) %>" />
			</portlet:renderURL>

			<aui:button href="<%= viewPatcherBuildURL %>" value="view-build" />
		</c:if>

		<c:if test="<%= (patcherFixPack.getStatus() == WorkflowConstants.STATUS_FIX_PACK_FROZEN) && (patcherFix != null) && Validator.isNull(patcherFix.getGitHash()) %>">
			<portlet:actionURL name="/patcher/set_build_fix_packs" var="mergePatcherFixPackURL">
				<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />
				<portlet:param name="redirect" value="<%= Validator.isNotNull(redirect) ? redirect : viewPatcherFixPacksURL %>" />
			</portlet:actionURL>

			<aui:button href="<%= mergePatcherFixPackURL %>" value="merge" />
		</c:if>
	</aui:button-row>
</aui:form>

<c:if test="<%= (jenkinsRequestParameters != null) && !jenkinsRequestParameters.isEmpty() %>">
	<div class="layout">
		<div class="layout-content">
			<clay:col
				size="4"
			>
				<aui:field-wrapper label="jenkins-request-parameters" />
			</clay:col>

			<clay:col
				size="8"
			>
				<aui:field-wrapper label="value" />
			</clay:col>
		</div>
	</div>

	<%
	for (Map.Entry<String, String> jenkinsRequestParameter : jenkinsRequestParameters.entrySet()) {
	%>

		<div class="layout">
			<div class="layout-content">
				<clay:col
					size="4"
				>
					<%= jenkinsRequestParameter.getKey() %>
				</clay:col>

				<clay:col
					size="8"
				>
					<%= jenkinsRequestParameter.getValue() %>
				</clay:col>
			</div>
		</div>

	<%
	}
	%>

</c:if>