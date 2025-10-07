<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

long patcherBuildId = ParamUtil.getLong(request, "patcherBuildId");

PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherBuildId);

List<String> patcherBuildTickets = PatcherUtil.getTickets(patcherBuild.getName());

List<String> cumulativeFixedIssues = PatcherProjectVersionUtil.getCumulativePatcherProjectVersionFixedIssues(patcherBuild.getPatcherProjectVersionId());

patcherBuildTickets.retainAll(cumulativeFixedIssues);
%>

<liferay-ui:header
	title='<%= LanguageUtil.format(request, "edit-x", String.valueOf(patcherBuildId)) %>'
/>

<aui:model-context bean="<%= patcherBuild %>" model="<%= PatcherBuild.class %>" />

<c:if test="<%= !patcherBuildTickets.isEmpty() %>">
	<clay:alert
		displayType="warning"
	>
		<liferay-ui:message arguments="<%= StringUtil.merge(patcherBuildTickets, StringPool.COMMA_AND_SPACE) %>" key="the-tickets-x-will-be-removed-from-the-list-since-they-are-included-in-the-project-version" />
	</clay:alert>
</c:if>

<portlet:actionURL name="/patcher/update_builds" var="updatePatcherBuildURL" />

<liferay-frontend:edit-form
	action="<%= updatePatcherBuildURL %>"
	fluid="<%= true %>"
	method="post"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="patcherBuildId" type="hidden" value="<%= patcherBuild.getPatcherBuildId() %>" />

	<liferay-frontend:edit-form-body>
		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="modified-date" />
			</p>

			<p class="text-secondary">
				<%= dateTimeFormat.format(patcherBuild.getModifiedDate()) %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="created-by" />
			</p>

			<p class="text-secondary">
				<%= patcherBuild.getUserName() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="build-id" />
			</p>

			<p class="text-secondary">
				<%= patcherBuild.getPatcherBuildId() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="version" />
			</p>

			<p class="text-secondary">
				<%= patcherBuild.getKeyVersion() %>
			</p>
		</div>

		<%
		PatcherProductVersion patcherProductVersion = PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(patcherBuild.getPatcherProductVersionId());
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
		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherBuild.getPatcherProjectVersionId());
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
				<liferay-ui:message key="tickets-list" />
			</p>

			<p class="text-secondary">
				<%= patcherBuild.getName() %>
			</p>
		</div>

		<%
		PatcherAccount patcherAccount = PatcherAccountLocalServiceUtil.getPatcherAccount(patcherBuild.getPatcherAccountId());
		%>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="account-code" />
			</p>

			<p class="text-secondary">
				<%= patcherAccount.getAccountEntryCode() %>
			</p>
		</div>

		<aui:input name="supportTicket" type="text" />

		<aui:select name="type">
			<aui:option label="<%= PatcherBuildConstants.LABEL_OFFICIAL %>" value="<%= PatcherBuildConstants.TYPE_OFFICIAL %>" />
			<aui:option label="<%= PatcherBuildConstants.LABEL_DEBUG %>" value="<%= PatcherBuildConstants.TYPE_DEBUG %>" />
			<aui:option label="<%= PatcherBuildConstants.LABEL_IGNORE %>" value="<%= PatcherBuildConstants.TYPE_IGNORE %>" />
		</aui:select>

		<aui:input name="mergeOnly" type="checkbox" value="<%= PatcherBuildUtil.isMergeOnly(patcherBuild) %>" />

		<aui:input name="smokeTestOnly" type="checkbox" wrapperCssClass="d-none" />
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= redirect %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>