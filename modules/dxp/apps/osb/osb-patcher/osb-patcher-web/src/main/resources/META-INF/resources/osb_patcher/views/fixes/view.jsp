<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherViewFixesDisplayContext patcherViewFixesDisplayContext = new PatcherViewFixesDisplayContext(request, renderRequest, renderResponse);

String redirect = ParamUtil.getString(request, "redirect");

PatcherFix patcherFix = patcherViewFixesDisplayContext.getPatcherFix();

PatcherFix latestPatcherFix = null;

if (!patcherFix.isLatestFix()) {
	latestPatcherFix = PatcherFixUtil.fetchPatcherFixByLatestFix(patcherFix.getKey());
}
%>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="fixes" />
	</liferay-util:include>

	<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
		<liferay-util:param name="title" value="view-fix" />
		<liferay-util:param name="mvcRenderCommandName" value="/patcher/index_fixes" />
	</liferay-util:include>
</c:if>

<aui:model-context bean="<%= patcherFix %>" model="<%= PatcherFix.class %>" />

<portlet:renderURL var="viewPatcherFixURL">
	<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes" />
	<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />

	<c:if test="<%= Validator.isNotNull(redirect) %>">
		<portlet:param name="redirect" value="<%= redirect %>" />
	</c:if>
</portlet:renderURL>

<c:if test="<%= !patcherFix.isLatestFix() %>">
	<liferay-ui:message key="this-is-not-the-latest-fix-version-view-the-latest-fix-here" />

	<portlet:renderURL var="viewLatestPatcherFixURL">
		<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes" />
		<portlet:param name="patcherFixId" value="<%= String.valueOf(latestPatcherFix.getPatcherFixId()) %>" />
		<portlet:param name="redirect" value="<%= viewPatcherFixURL %>" />
	</portlet:renderURL>

	<a href="<%= viewLatestPatcherFixURL %>">
		<%= latestPatcherFix.getPatcherFixId() %>
	</a>
</c:if>

<aui:field-wrapper label="modified-date">
	<fmt:formatDate
		type="both"
		value="<%= patcherFix.getModifiedDate() %>"
	/>
</aui:field-wrapper>

<aui:field-wrapper label="status-date">
	<fmt:formatDate
		type="both"
		value="<%= patcherFix.getStatusDate() %>"
	/>
</aui:field-wrapper>

<aui:field-wrapper label="created-by">
	<%= patcherFix.getUserName() %>
</aui:field-wrapper>

<aui:field-wrapper label="status-updated-by">
	<%= patcherFix.getStatusByUserName() %>
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

<aui:select disabled="<%= true %>" label="product-version" name="patcherProductVersionId" showEmptyOption="<%= true %>">

	<%
	for (PatcherProductVersion patcherProductVersion : PatcherProductVersionUtil.getPatcherProductVersions()) {
	%>

		<aui:option label="<%= patcherProductVersion.getName() %>" value="<%= patcherProductVersion.getPatcherProductVersionId() %>" />

	<%
	}
	%>

</aui:select>

<aui:select disabled="<%= true %>" label="project-version" name="patcherProjectVersionId" showEmptyOption="<%= false %>">

	<%
	for (PatcherProjectVersion patcherProjectVersion : PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions()) {
	%>

		<aui:option label="<%= patcherProjectVersion.getName() %>" value="<%= patcherProjectVersion.getPatcherProjectVersionId() %>" />

	<%
	}
	%>

</aui:select>

<aui:field-wrapper label="git-hash">
	<a href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()) %>" target="_blank"><%= patcherFix.getGitHash() %></a>
</aui:field-wrapper>

<aui:field-wrapper label="jenkins">

	<%
	for (Map<String, String> jenkinsResult : JenkinsUtil.getJenkinsResults(patcherFix)) {
	%>

		<clay:link
			cssClass="nobr"
			href='<%= jenkinsResult.get("statusURL") %>'
			target="_blank"
			title='<%= jenkinsResult.get("jobName") %>'
		/>

	<%
	}
	%>

</aui:field-wrapper>

<aui:input inputCssClass="osb-patcher-input-wide osb-patcher-read-only" label="content" name="patcherFixName" readonly="<%= true %>" type="textarea" value="<%= patcherFix.getName() %>" />

<aui:input inputCssClass="osb-patcher-read-only" label="branch-name" name="committish" readonly="<%= true %>" type="text" />

<aui:input inputCssClass="osb-patcher-read-only" label="github-url" name="gitRemoteURL" readonly="<%= true %>" type="text" />

<aui:select disabled="<%= true %>" name="type" showEmptyOption="<%= false %>">
	<aui:option label="<%= PatcherFixConstants.LABEL_EXCLUDED %>" value="<%= PatcherFixConstants.TYPE_EXCLUDED %>" />
	<aui:option label="<%= PatcherFixConstants.LABEL_GENERATED %>" value="<%= PatcherFixConstants.TYPE_GENERATED %>" />
	<aui:option label="<%= PatcherFixConstants.LABEL_GENERATED_PRIVATE_PUBLIC %>" value="<%= PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC %>" />
	<aui:option label="<%= PatcherFixConstants.LABEL_PATCH %>" value="<%= PatcherFixConstants.TYPE_PATCH %>" />
	<aui:option label="<%= PatcherFixConstants.LABEL_REBASE %>" value="<%= PatcherFixConstants.TYPE_REBASE %>" />
	<aui:option label="<%= PatcherFixConstants.LABEL_WORKAROUND %>" value="<%= PatcherFixConstants.TYPE_WORKAROUND %>" />
</aui:select>

<aui:button-row>
	<c:if test="<%= patcherFix.isLatestFix() %>">
		<portlet:renderURL var="editPatcherFixURL">
			<portlet:param name="mvcRenderCommandName" value="/edit_fixes" />
			<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
			<portlet:param name="redirect" value="<%= viewPatcherFixURL %>" />
		</portlet:renderURL>

		<aui:button href="<%= editPatcherFixURL %>" value="edit" />
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

<%
SearchContainer<PatcherFix> patcherFixSearchContainer = patcherViewFixesDisplayContext.getSearchContainer();
%>

<c:if test="<%= patcherFixSearchContainer.getTotal() > 1 %>">
	<aui:field-wrapper label="fix-versions" />

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

			<portlet:renderURL var="viewPatcherFixKeyVersionURL">
				<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes" />
				<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFixKeyVersion.getPatcherFixId()) %>" />
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

			<liferay-ui:search-container-column-text
				cssClass="osb-patcher-user-display"
				name="created-by"
			>
				<liferay-ui:user-display
					displayStyle="<%= 1 %>"
					url="<%= PatcherUtil.getUserDisplayURL(themeDisplay, patcherFixKeyVersion.getUserId()) %>"
					userId="<%= patcherFixKeyVersion.getUserId() %>"
					userName="<%= patcherFixKeyVersion.getUserName() %>"
				/>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="modified-date"
			>
				<fmt:formatDate
					type="both"
					value="<%= patcherFixKeyVersion.getModifiedDate() %>"
				/>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherFixKeyVersion.getPatcherFixId()) %>"
				name="git-hash"
				target="_blank"
				value="<%= patcherFixKeyVersion.getGitHash() %>"
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
			paginate="<%= false %>"
		/>
	</liferay-ui:search-container>
</c:if>

<aui:script>
	function <portlet:namespace />handleClick(title, url) {
		Liferay.Util.openModal({
			title: title,
			url: url,
		});
	}

	YUI().ready(
		'aui-popover',
		function(Y) {
			var align_points = [Y.WidgetPositionAlign.LC, Y.WidgetPositionAlign.RC];
			var tickets = document.getElementById('<portlet:namespace />patcherFixName');
			var trigger = Y.one('#<portlet:namespace />patcherFixName');

			Liferay.Patcher.getTicketLinksPopover(Y, align_points, tickets, trigger)
		}
	);
</aui:script>