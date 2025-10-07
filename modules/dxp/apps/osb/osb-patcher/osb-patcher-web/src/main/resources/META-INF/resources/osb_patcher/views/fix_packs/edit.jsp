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

boolean released = patcherFixPack.getStatus() == WorkflowConstants.STATUS_FIX_PACK_RELEASED;
%>

<liferay-ui:header
	title='<%= LanguageUtil.format(request, "edit-x", String.valueOf(patcherFixPack.getPatcherFixPackId())) %>'
/>

<aui:model-context bean="<%= patcherFixPack %>" model="<%= PatcherFixPack.class %>" />

<portlet:actionURL name="/patcher/update_fix_packs" var="updatePatcherFixPackURL" />

<liferay-frontend:edit-form
	action="<%= updatePatcherFixPackURL %>"
	fluid="<%= true %>"
	method="post"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="patcherFixPackId" type="hidden" value="<%= patcherFixPack.getPatcherFixPackId() %>" />

	<liferay-frontend:edit-form-body>
		<c:if test="<%= patcherFix != null %>">
			<div class="c-mb-3">
				<p class="c-mb-1 font-weight-semi-bold text-3">
					<liferay-ui:message key="git-hash" />
				</p>

				<p class="text-secondary">
					<a href="<%= gitHubURL %>" target="_blank"><%= patcherFix.getGitHash() %></a>
				</p>
			</div>
		</c:if>

		<%
		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherFixPack.getPatcherProjectVersionId());
		%>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="project-version" />
			</p>

			<p class="text-secondary">
				<%= patcherProjectVersion.getName() %>
			</p>
		</div>

		<%
		PatcherFixComponent patcherFixComponent = PatcherFixComponentLocalServiceUtil.fetchPatcherFixComponent(patcherFixPack.getPatcherFixComponentId());
		%>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="component" />
			</p>

			<p class="text-secondary">
				<%= patcherFixComponent.getName() %>
			</p>
		</div>

		<aui:input name="version" type="hidden" value="<%= patcherFixPack.getVersion() %>" />

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="version" />
			</p>

			<p class="text-secondary">
				<%= patcherFixPack.getVersion() %>
			</p>
		</div>

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
			<div class="c-mb-3">
				<p class="c-mb-1 font-weight-semi-bold text-3">
					<liferay-ui:message key="released-date" />
				</p>

				<p class="text-secondary">
					<%= dateTimeFormat.format(patcherFixPack.getReleasedDate()) %>
				</p>
			</div>
		</c:if>

		<aui:input disabled="<%= released %>" name="requirements" />
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<aui:button disabled="<%= released %>" type="submit" value="save" />

		<aui:button href="<%= redirect %>" value="cancel" />

		<c:if test="<%= (patcherFix != null) && Validator.isNotNull(patcherFix.getGitHash()) %>">
			<portlet:actionURL name="/patcher/build_fix_packs" var="buildPatcherBuildURL">
				<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />
				<portlet:param name="redirect" value="<%= redirect %>" />
			</portlet:actionURL>

			<aui:button href="<%= buildPatcherBuildURL %>" value="build" />
		</c:if>

		<c:if test="<%= patcherFixPack.getPatcherBuildId() > 0 %>">
			<portlet:renderURL var="viewPatcherBuildURL">
				<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds" />
				<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherFixPack.getPatcherBuildId()) %>" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
			</portlet:renderURL>

			<aui:button href="<%= viewPatcherBuildURL %>" value="view-build" />
		</c:if>

		<c:if test="<%= (patcherFixPack.getStatus() == WorkflowConstants.STATUS_FIX_PACK_FROZEN) && (patcherFix != null) && Validator.isNull(patcherFix.getGitHash()) %>">
			<portlet:actionURL name="/patcher/set_build_fix_packs" var="mergePatcherFixPackURL">
				<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />
				<portlet:param name="redirect" value="<%= redirect %>" />
			</portlet:actionURL>

			<aui:button href="<%= mergePatcherFixPackURL %>" value="merge" />
		</c:if>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<c:if test="<%= (jenkinsRequestParameters != null) && !jenkinsRequestParameters.isEmpty() %>">
	<clay:container-fluid
		cssClass="container-no-gutters"
	>
		<clay:sheet
			size="full"
		>
			<h2 class="sheet-title">
				<liferay-ui:message key="jenkins-request-parameters" />
			</h2>

			<table class="table table-striped">
				<thead>
					<tr>
						<th><liferay-ui:message key="parameter" /></th>
						<th><liferay-ui:message key="value" /></th>
					</tr>
				</thead>

				<tbody>

					<%
					for (Map.Entry<String, String> jenkinsRequestParameter : jenkinsRequestParameters.entrySet()) {
					%>

						<tr>
							<td><%= jenkinsRequestParameter.getKey() %></td>
							<td><%= jenkinsRequestParameter.getValue() %></td>
						</tr>

					<%
					}
					%>

				</tbody>
			</table>
		</clay:sheet>
	</clay:container-fluid>
</c:if>