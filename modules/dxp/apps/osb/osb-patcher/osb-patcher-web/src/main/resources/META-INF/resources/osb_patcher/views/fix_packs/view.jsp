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

PatcherFixPack patcherFixPack = PatcherFixPackLocalServiceUtil.fetchPatcherFixPack(patcherFixPackId);

PatcherFixComponent patcherFixComponent = PatcherFixComponentLocalServiceUtil.fetchPatcherFixComponent(patcherFixPack.getPatcherFixComponentId());

PatcherFix mainPatcherFix = null;

PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherFixPack.getPatcherBuildId());

if (patcherBuild != null) {
	mainPatcherFix = PatcherFixLocalServiceUtil.getPatcherFix(patcherBuild.getPatcherFixId());
}
%>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="fix-packs" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="view-fix-pack" />
	<liferay-util:param name="mvcRenderCommandName" value="/patcher/index_fix_packs" />
</liferay-util:include>

<c:if test="<%= (mainPatcherFix != null) && Validator.isNotNull(mainPatcherFix.getGitHash()) %>">
	<aui:field-wrapper label="git-hash">
		<a href="<%= PatcherFixUtil.getPatcherFixGitHubURL(mainPatcherFix.getPatcherFixId()) %>" target="_blank"><%= mainPatcherFix.getGitHash() %></a>
	</aui:field-wrapper>
</c:if>

<aui:field-wrapper label="name">
	<%= patcherFixPack.getName() %>
</aui:field-wrapper>

<aui:field-wrapper label="component">
	<%= patcherFixComponent.getName() %>
</aui:field-wrapper>

<aui:field-wrapper label="version">
	<%= patcherFixPack.getVersion() %>
</aui:field-wrapper>

<aui:field-wrapper label="status">
	<liferay-ui:message key="<%= WorkflowConstants.getStatusLabel(patcherFixPack.getStatus()) %>" />
</aui:field-wrapper>

<c:if test="<%= patcherBuild != null %>">
	<aui:field-wrapper label="build-status">
		<liferay-ui:message key="<%= WorkflowConstants.getStatusLabel(patcherBuild.getStatus()) %>" />
	</aui:field-wrapper>
</c:if>

<c:if test="<%= (patcherBuild != null) && Validator.isNotNull(patcherBuild.getQaStatus()) %>">
	<aui:field-wrapper label="qa-status">
		<liferay-ui:message key="<%= PatcherBuildUtil.getQAStatusLabel(patcherFixPack.getPatcherBuildId()) %>" />
	</aui:field-wrapper>
</c:if>

<aui:field-wrapper label="released-date">
	<fmt:formatDate
		value="<%= patcherFixPack.getReleasedDate() %>"
	/>
</aui:field-wrapper>

<aui:field-wrapper label="requirements">
	<%= patcherFixPack.getRequirements() %>
</aui:field-wrapper>

<aui:field-wrapper label="new-issues">
	<%= StringUtil.merge(PatcherUtil.getNewTickets(patcherFixPack), StringPool.COMMA_AND_SPACE) %>
</aui:field-wrapper>

<aui:field-wrapper label="overridden-issues">
	<%= StringUtil.merge(PatcherUtil.getOverriddenTickets(patcherFixPack), StringPool.COMMA_AND_SPACE) %>
</aui:field-wrapper>

<aui:field-wrapper label="oldest-fix-date">
	<fmt:formatDate
		value="<%= PatcherFixUtil.getOldestPatcherFixCreateDate(patcherFixPack.getPatcherFixPackId()) %>"
	/>
</aui:field-wrapper>

<aui:button-row>
	<portlet:renderURL var="viewPatcherFixPackURL">
		<portlet:param name="mvcRenderCommandName" value="/patcher/view_fix_packs" />
		<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />

		<c:if test="<%= Validator.isNotNull(redirect) %>">
			<portlet:param name="redirect" value="<%= redirect %>" />
		</c:if>
	</portlet:renderURL>

	<portlet:renderURL var="editPatcherFixPackURL">
		<portlet:param name="mvcRenderCommandName" value="/patcher/edit_fix_packs" />
		<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />
		<portlet:param name="redirect" value="<%= viewPatcherFixPackURL %>" />
	</portlet:renderURL>

	<aui:button href="<%= editPatcherFixPackURL %>" value="edit" />

	<c:if test="<%= patcherFixPack.getPatcherBuildId() > 0 %>">
		<portlet:renderURL var="viewPatcherBuildURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds" />
			<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherFixPack.getPatcherBuildId()) %>" />
		</portlet:renderURL>

		<aui:button href="<%= viewPatcherBuildURL %>" value="view-build" />
	</c:if>

	<c:if test="<%= (patcherBuild != null) && (patcherBuild.getStatus() == WorkflowConstants.STATUS_BUILD_COMPLETE) %>">
		<portlet:actionURL name="/patcher/test_builds" var="testPatcherFixPackURL">
			<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherFixPack.getPatcherBuildId()) %>" />
			<portlet:param name="redirect" value="<%= viewPatcherFixPackURL %>" />
		</portlet:actionURL>

		<aui:button href="<%= testPatcherFixPackURL %>" value="test" />
	</c:if>
</aui:button-row>

<aui:field-wrapper label="fixes-in-fix-pack" />

<%
List<PatcherFix> patcherFixes = PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixes(patcherFixPack.getPatcherFixPackId());
%>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-fixes"
	total="<%= patcherFixes.size() %>"
>
	<liferay-ui:search-container-results
		results="<%= patcherFixes %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherFix"
		escapedModel="<%= true %>"
		keyProperty="patcherFixId"
		modelVar="patcherFix"
	>
		<portlet:renderURL var="viewPatcherFixURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes" />
			<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			href="<%= viewPatcherFixURL %>"
			name="fix-id"
			property="patcherFixId"
		/>

		<liferay-ui:search-container-column-text
			name="name"
		>

			<%
			for (String token : StringUtil.split(patcherFix.getName())) {
			%>

				<a class="nobr" href="<%= patcherConfiguration.jiraURL() %>/<%= token %>" target="_blank"><%= token %></a>,

			<%
			}
			%>

		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<liferay-ui:search-container-column-text
			href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()) %>"
			name="git-hash"
			target="_blank"
			value="<%= patcherFix.getGitHash() %>"
		/>

		<liferay-ui:search-container-column-text
			property="dependencies"
		/>

		<liferay-ui:search-container-column-text
			name="status"
			value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherFix.getStatus())) %>"
		/>

		<liferay-ui:search-container-column-text
			name="fix-pack-status"
		>
			<c:if test="<%= patcherFix.getFixPackStatus() > 0 %>">
				<liferay-ui:message key="<%= WorkflowConstants.getStatusLabel(patcherFix.getFixPackStatus()) %>" />
			</c:if>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="type"
			value="<%= LanguageUtil.get(request, PatcherFixConstants.getTypeLabel(patcherFix.getType())) %>"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT, patcherFix.getUserId()) && patcherFix.getLatestFix() %>">
					<portlet:renderURL var="editPatcherFixURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/edit_fixes" />
						<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="<%= editPatcherFixURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT_FIX_PACK_FIELDS, patcherFix.getUserId()) %>">
					<portlet:renderURL var="editPatcherFixFixPackFieldsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/patcher/edit_fix_pack_fields_fixes" />
						<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
					</portlet:renderURL>

					<clay:button
						displayType="link"
						label='<%= LanguageUtil.get(request, "edit-fix-packs") %>'
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + editPatcherFixFixPackFieldsURL + "');" %>'
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.SET_FIX_PACK_FIELDS, patcherFix.getUserId()) %>">
					<portlet:renderURL var="viewPatcherFixPackURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/view_fix_packs" />
						<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />
					</portlet:renderURL>

					<portlet:actionURL name="/patcher/set_fix_pack_fields_fixes" var="setFixPackFieldsURL">
						<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
						<portlet:param name="redirect" value="<%= viewPatcherFixPackURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="remove"
						message="remove-fix-packs"
						url="<%= setFixPackFieldsURL %>"
					/>
				</c:if>

				<c:if test="<%= false %>">
					<portlet:actionURL name="/patcher/delete_fixes" var="deletePatcherFixURL">
						<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon-delete
						url="<%= deletePatcherFixURL %>"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		paginate="<%= false %>"
	/>
</liferay-ui:search-container>

<%
Set<PatcherFixPack> prerequisitePatcherFixPacks = PatcherFixPackUtil.getPrerequisitePatcherFixPacks(patcherFixPack.getPatcherFixPackId());
%>

<c:if test="<%= !prerequisitePatcherFixPacks.isEmpty() %>">
	<aui:field-wrapper label="dependencies" />

	<liferay-ui:search-container
		total="<%= prerequisitePatcherFixPacks.size() %>"
	>
		<liferay-ui:search-container-results
			results="<%= ListUtil.fromCollection(prerequisitePatcherFixPacks) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.osb.patcher.model.PatcherFixPack"
			escapedModel="<%= true %>"
			keyProperty="patcherFixPackId"
			modelVar="curPatcherFixPack"
		>
			<portlet:renderURL var="viewPatcherFixPackURL">
				<portlet:param name="mvcRenderCommandName" value="/patcher/view_fix_packs" />
				<portlet:param name="patcherFixPackId" value="<%= String.valueOf(curPatcherFixPack.getPatcherFixPackId()) %>" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				href="<%= viewPatcherFixPackURL %>"
				name="name"
				value="<%= curPatcherFixPack.getName() %>"
			/>

			<liferay-ui:search-container-column-text
				name="component"
				value="<%= patcherFixComponent.getName() %>"
			/>

			<liferay-ui:search-container-column-text
				name="version"
				value="<%= String.valueOf(curPatcherFixPack.getVersion()) %>"
			/>

			<%
			PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(curPatcherFixPack.getPatcherProjectVersionId());
			%>

			<liferay-ui:search-container-column-text
				name="project-version"
				value="<%= patcherProjectVersion.getName() %>"
			/>

			<liferay-ui:search-container-column-text
				name="status"
				value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(curPatcherFixPack.getStatus())) %>"
			/>

			<liferay-ui:search-container-column-text
				name="released-date"
			>
				<fmt:formatDate
					value="<%= curPatcherFixPack.getReleasedDate() %>"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			paginate="<%= false %>"
		/>
	</liferay-ui:search-container>
</c:if>

<aui:script>
	function <portlet:namespace />handleClick(url) {
		Liferay.Util.openModal({
			title: '<liferay-ui:message key="edit-fix-packs" />',
			url: url,
		});
	}
</aui:script>