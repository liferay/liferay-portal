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

<liferay-ui:header
	title='<%= LanguageUtil.format(request, "edit-x", String.valueOf(patcherFix.getPatcherFixId())) %>'
/>

<aui:model-context bean="<%= patcherFix %>" model="<%= PatcherFix.class %>" />

<portlet:actionURL name="/patcher/update_fixes" var="updatePatcherFixURL" />

<liferay-frontend:edit-form
	action="<%= updatePatcherFixURL %>"
	fluid="<%= true %>"
	method="post"
	name="fm"
>
	<liferay-frontend:edit-form-body>
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="patcherFixId" type="hidden" value="<%= patcherFix.getPatcherFixId() %>" />

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="modified-date" />
			</p>

			<p class="text-secondary">
				<%= dateTimeFormat.format(patcherFix.getModifiedDate()) %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="created-by" />
			</p>

			<p class="text-secondary">
				<%= patcherFix.getUserName() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="fix-id" />
			</p>

			<p class="text-secondary">
				<%= patcherFix.getPatcherFixId() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="version" />
			</p>

			<p class="text-secondary">
				<%= patcherFix.getKeyVersion() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="patcher-status" />
			</p>

			<liferay-portal-workflow:status
				showStatusLabel="<%= false %>"
				status="<%= patcherFix.getStatus() %>"
				statusMessage="<%= WorkflowConstants.getStatusLabel(patcherFix.getStatus()) %>"
			/>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="created-by" />
			</p>

			<p class="text-secondary">
				<%= patcherFix.getUserName() %>
			</p>
		</div>

		<%
		PatcherProductVersion patcherProductVersion = PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(patcherFix.getPatcherProductVersionId());
		%>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="product-version" />
			</p>

			<p class="text-secondary">
				<%= patcherProductVersion.getName() %>
			</p>
		</div>

		<%
		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherFix.getPatcherProjectVersionId());
		%>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="project-version" />
			</p>

			<p class="text-secondary">
				<%= patcherProjectVersion.getName() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="git-hash" />
			</p>

			<p class="text-secondary">
				<a href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()) %>" target="_blank"><%= patcherFix.getGitHash() %></a>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="content" />
			</p>

			<p class="text-secondary">
				<%= patcherFix.getName() %>
			</p>
		</div>

		<aui:input label="branch-name" name="committish" />

		<aui:input label="github-url" name="gitRemoteURL" />

		<aui:input name="workaround" type="checkbox" value="<%= patcherFix.getType() == PatcherFixConstants.TYPE_WORKAROUND %>" />
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<aui:button type="submit" value="update" />

		<aui:button href="<%= redirect %>" value="cancel" />

		<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT_FIX_PACK_FIELDS, patcherFix.getUserId()) %>">
			<clay:button
				displayType="secondary"
				label='<%= LanguageUtil.get(request, "edit-fix-packs") %>'
				onClick='<%= liferayPortletResponse.getNamespace() + "handleClick()" %>'
			/>
		</c:if>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script>
	function <portlet:namespace />handleClick() {
		Liferay.Util.openModal({
			title: '<liferay-ui:message key="edit-fix-packs" />',
			url: '<portlet:renderURL windowState="<%= LiferayWindowState.POP_UP.toString() %>"><portlet:param name="mvcRenderCommandName" value="/patcher/edit_fix_pack_fields_fixes" /><portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" /></portlet:renderURL>',
		});
	}
</aui:script>