<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherViewFixesDisplayContext patcherViewFixesDisplayContext = new PatcherViewFixesDisplayContext(request, renderRequest, renderResponse);

PatcherFix patcherFix = patcherViewFixesDisplayContext.getPatcherFix();

PatcherFix latestPatcherFix = null;

if (!patcherFix.isLatestFix()) {
	latestPatcherFix = PatcherFixUtil.fetchPatcherFixByLatestFix(patcherFix.getKey());
}
%>

<liferay-ui:header
	title="view-fix"
/>

<aui:model-context bean="<%= patcherFix %>" model="<%= PatcherFix.class %>" />

<c:if test="<%= !patcherFix.isLatestFix() %>">
	<portlet:renderURL var="viewLatestPatcherFixURL">
		<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes" />
		<portlet:param name="patcherFixId" value="<%= String.valueOf(latestPatcherFix.getPatcherFixId()) %>" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
	</portlet:renderURL>

	<clay:alert
		displayType="warning"
	>
		<clay:link
			href="<%= viewLatestPatcherFixURL %>"
			label="this-is-not-the-latest-fix-version-view-the-latest-fix-here"
		/>
	</clay:alert>
</c:if>

<clay:container-fluid
	cssClass="container-form-lg container-no-gutters"
>
	<clay:sheet
		size="full"
	>
		<h2 class="sheet-title">
			<liferay-ui:message key="view-fix" />
		</h2>

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
				<liferay-ui:message key="status-date" />
			</p>

			<p class="text-secondary">
				<%= dateTimeFormat.format(patcherFix.getStatusDate()) %>
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
				<liferay-ui:message key="status-updated-by" />
			</p>

			<p class="text-secondary">
				<%= patcherFix.getStatusByUserName() %>
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
				<liferay-ui:message key="jenkins" />
			</p>

			<p class="text-secondary">

				<%
				for (Map<String, String> jenkinsResult : JenkinsUtil.getJenkinsResults(patcherFix)) {
				%>

					<clay:link
						cssClass="nobr"
						href='<%= jenkinsResult.get("statusURL") %>'
						label='<%= jenkinsResult.get("jobName") %>'
						target="_blank"
					/>

				<%
				}
				%>

			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="content" />
			</p>

			<p class="text-secondary">

				<%
				List<String> tickets = ListUtil.sort(StringUtil.split(patcherFix.getName()));

				for (String ticket : tickets) {
				%>

					<clay:link
						href='<%= "https://liferay.atlassian.net/browse/" + ticket %>'
						label="<%= ticket %>"
						target="_blank"
					/>

					<%= StringPool.NBSP %>

				<%
				}
				%>

			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="branch-name" />
			</p>

			<p class="text-secondary">
				<%= patcherFix.getCommittish() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="github-url" />
			</p>

			<p class="text-secondary">
				<%= patcherFix.getGitRemoteURL() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="type" />
			</p>

			<p class="text-secondary">
				<%= PatcherFixConstants.getTypeLabel(patcherFix.getType()) %>
			</p>
		</div>

		<aui:button-row>
			<c:if test="<%= patcherFix.isLatestFix() %>">
				<portlet:renderURL var="editPatcherFixURL">
					<portlet:param name="mvcRenderCommandName" value="/patcher/edit_fixes" />
					<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
				</portlet:renderURL>

				<aui:button href="<%= editPatcherFixURL %>" primary="<%= true %>" value="edit" />
			</c:if>

			<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
				<portlet:renderURL var="viewPatcherBuildsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
					<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds_fixes" />
					<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
				</portlet:renderURL>

				<clay:button
					displayType="secondary"
					label='<%= LanguageUtil.get(request, "view-builds") %>'
					onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-builds-for-fix-id-x", patcherFix.getPatcherFixId()) + "', '" + viewPatcherBuildsURL + "');" %>'
				/>

				<portlet:renderURL var="viewPatcherFixesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
					<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes_fixes" />
					<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
				</portlet:renderURL>

				<clay:button
					displayType="secondary"
					label='<%= LanguageUtil.get(request, "view-fixes") %>'
					onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-fixes-for-fix-id-x", patcherFix.getPatcherFixId()) + "', '" + viewPatcherFixesURL + "');" %>'
				/>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT_FIX_PACK_FIELDS, patcherFix.getUserId()) && (patcherFix.getPatcherFixId() > 0) %>">
					<portlet:renderURL var="editPatcherFixFixPackFieldsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/patcher/edit_fix_pack_fields_fixes" />
						<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
					</portlet:renderURL>

					<clay:button
						displayType="secondary"
						label='<%= LanguageUtil.get(request, "edit-fix-packs") %>'
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.get(request, "edit-fix-packs") + "', '" + editPatcherFixFixPackFieldsURL + "');" %>'
					/>
				</c:if>
			</c:if>
		</aui:button-row>
	</clay:sheet>
</clay:container-fluid>

<%
SearchContainer<PatcherFix> patcherFixSearchContainer = patcherViewFixesDisplayContext.getSearchContainer();
%>

<c:if test="<%= patcherFixSearchContainer.getTotal() > 1 %>">
	<clay:container-fluid
		cssClass="container-no-gutters"
	>
		<clay:sheet
			size="full"
		>
			<h2 class="sheet-title">
				<liferay-ui:message key="fix-versions" />
			</h2>

			<liferay-ui:search-container
				searchContainer="<%= patcherFixSearchContainer %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.osb.patcher.model.PatcherFix"
					escapedModel="<%= true %>"
					keyProperty="patcherFixId"
					modelVar="patcherFixKeyVersion"
				>
					<c:if test="<%= patcherFix.getPatcherFixId() == patcherFixKeyVersion.getPatcherFixId() %>">
						<liferay-ui:search-container-row-parameter
							name="className"
							value="selected"
						/>
					</c:if>

					<liferay-ui:search-container-column-user
						name="created-by"
						userId="<%= patcherFixKeyVersion.getUserId() %>"
					/>

					<portlet:renderURL var="viewPatcherFixKeyVersionURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes" />
						<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFixKeyVersion.getPatcherFixId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:renderURL>

					<liferay-ui:search-container-column-text
						href="<%= (patcherFix.getPatcherFixId() != patcherFixKeyVersion.getPatcherFixId()) ? viewPatcherFixKeyVersionURL : null %>"
						name="fix-id"
						property="patcherFixId"
					/>

					<liferay-ui:search-container-column-text
						name="version"
						property="keyVersion"
					/>

					<liferay-ui:search-container-column-date
						name="modified-date"
						value="<%= patcherFixKeyVersion.getModifiedDate() %>"
					/>

					<liferay-ui:search-container-column-text
						href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherFixKeyVersion.getPatcherFixId()) %>"
						name="git-hash"
						target="_blank"
						value="<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherFixKeyVersion.getGitHash(), 10) %>"
					/>

					<liferay-ui:search-container-column-text
						name="patcher-status"
						value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherFixKeyVersion.getStatus())) %>"
					/>

					<liferay-ui:search-container-column-text
						name="type"
						value="<%= LanguageUtil.get(request, PatcherFixConstants.getTypeLabel(patcherFixKeyVersion.getType())) %>"
					/>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
					paginate="<%= false %>"
				/>
			</liferay-ui:search-container>
		</clay:sheet>
	</clay:container-fluid>
</c:if>

<aui:script>
	function <portlet:namespace />handleClick(title, url) {
		Liferay.Util.openModal({
			title: title,
			url: url,
		});
	}
</aui:script>