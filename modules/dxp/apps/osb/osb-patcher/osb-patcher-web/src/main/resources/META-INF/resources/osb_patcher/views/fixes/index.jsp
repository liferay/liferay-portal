<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherFixesDisplayContext patcherFixesDisplayContext = new PatcherFixesDisplayContext(request, renderRequest, renderResponse);
%>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="fixes" />
</liferay-util:include>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new PatcherFixesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, patcherFixesDisplayContext.getSearchContainer()) %>"
/>

<liferay-ui:search-container
	searchContainer="<%= patcherFixesDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherFix"
		escapedModel="<%= true %>"
		keyProperty="patcherFixId"
		modelVar="patcherFix"
	>
		<liferay-ui:search-container-column-text
			cssClass="osb-patcher-search-container-column-text-icon"
		>
			<c:if test="<%= patcherFix.isObsolete() %>">
				<portlet:renderURL var="viewPatcherFixPopUpURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
					<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes" />
					<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
				</portlet:renderURL>

				<clay:button
					displayType="link"
					icon="../common/activate"
					label='<%= LanguageUtil.get(request, "this-fix-is-obsolete") %>'
					onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.get(request, "view-fix") + "', '" + viewPatcherFixPopUpURL + "');" %>'
				/>
			</c:if>
		</liferay-ui:search-container-column-text>

		<portlet:renderURL var="viewPatcherFixURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes" />
			<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			href="<%= viewPatcherFixURL %>"
			name="fix-id"
			property="patcherFixId"
		/>

		<liferay-ui:search-container-column-text
			name="content"
		>

			<%
			for (String token : StringUtil.split(patcherFix.getName())) {
			%>

				<c:choose>
					<c:when test="<%= PatcherFixPackUtil.containsPatcherFixPackName(token) %>">

						<%
						PatcherFixPack patcherFixPack = PatcherFixPackUtil.getPatcherFixPack(token, patcherFix.getPatcherProjectVersionId());
						%>

						<portlet:renderURL var="viewPatcherFixPackURL">
							<portlet:param name="mvcRenderCommandName" value="/patcher/view_fix_packs" />
							<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />
						</portlet:renderURL>

						<a class="nobr" href="<%= viewPatcherFixPackURL %>"><%= token %></a>,
					</c:when>
					<c:otherwise>
						<a class="nobr" href="<%= patcherConfiguration.jiraURL() %>/<%= token %>" target="_blank"><%= token %></a>,
					</c:otherwise>
				</c:choose>

			<%
			}
			%>

		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<%
		PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherFix.getPatcherProjectVersionId());
		%>

		<liferay-ui:search-container-column-text
			name="product-version"
			value="<%= PatcherProductVersionUtil.fetchPatcherProductVersionName(patcherProjectVersion.getPatcherProductVersionId()) %>"
		/>

		<liferay-ui:search-container-column-text
			name="project-version"
			value="<%= patcherProjectVersion.getName() %>"
		/>

		<liferay-ui:search-container-column-text
			href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()) %>"
			name="git-hash"
			target="_blank"
			value="<%= patcherFix.getGitHash() %>"
		/>

		<liferay-ui:search-container-column-text
			name="patcher-status"
			value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherFix.getStatus())) %>"
		/>

		<portlet:renderURL var="editPatcherFixCommentsFieldURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcRenderCommandName" value="/patcher/edit_comments_field_fixes" />
			<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			name="engineer-comments"
		>
			<c:choose>
				<c:when test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherFix.getUserId()) %>">
					<clay:button
						displayType="link"
						label="<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherFix.getComments(), 75) %>"
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "edit-engineer-comments-for-fix-id-x", patcherFix.getPatcherFixId()) + "', '" + editPatcherFixCommentsFieldURL + "');" %>'
					/>
				</c:when>
				<c:otherwise>
					<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherFix.getComments(), 75) %>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="jenkins"
		>

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

		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="type"
			value="<%= LanguageUtil.get(request, PatcherFixConstants.getTypeLabel(patcherFix.getType())) %>"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu
				cssClass="osb-patcher-icon-menu"
			>
				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT, patcherFix.getUserId()) && patcherFix.isLatestFix() && (patcherFix.getType() != PatcherFixConstants.TYPE_FIX_PACK) %>">
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

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherFix.getUserId()) %>">
					<liferay-ui:icon
						image="edit"
						message="edit-engineer-comments"
						method="get"
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.get(request, "edit-engineer-comments") + "', '" + editPatcherFixCommentsFieldURL + "');" %>'
						url="javascript:void(0);"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT_FIX_PACK_FIELDS, patcherFix.getUserId()) %>">
					<portlet:renderURL var="editPatcherFixFixPackFieldsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/patcher/edit_fix_pack_fields_fixes" />
						<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						message="edit-fix-packs"
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.get(request, "edit-fix-packs") + "', '" + editPatcherFixFixPackFieldsURL + "');" %>'
						url="javascript:void(0);"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.BUILDS, patcherFix.getUserId()) %>">
					<portlet:renderURL var="viewPatcherFixPatcherBuildsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds_fixes" />
						<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="view"
						message="view-builds"
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-builds-for-fix-id-x", patcherFix.getPatcherFixId()) + "', '" + viewPatcherFixPatcherBuildsURL + "');" %>'
						url="javascript:void(0);"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.FIXES, patcherFix.getUserId()) %>">
					<portlet:renderURL var="viewPatcherFixesPopUpURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes_fixes" />
						<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="view"
						message="view-fixes"
						onClick='<%= liferayPortletResponse.getNamespace() + "handleClick('" + UnicodeLanguageUtil.format(request, "view-fixes-for-fix-id-x", patcherFix.getPatcherFixId()) + "', '" + viewPatcherFixesPopUpURL + "');" %>'
						url="javascript:void(0);"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EXCLUDE, patcherFix.getUserId()) && (patcherFix.getType() != PatcherFixConstants.TYPE_EXCLUDED) && (patcherFix.getType() != PatcherFixConstants.TYPE_FIX_PACK) %>">
					<portlet:actionURL name="/patcher/exclude_fixes" var="excludePatcherFixURL">
						<portlet:param name="patcherFixId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<liferay-ui:icon
						image="../api/method"
						message="exclude"
						url="<%= excludePatcherFixURL %>"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>

<aui:script>
	function <portlet:namespace />handleClick(title, url) {
		Liferay.Util.openModal({
			title: title,
			url: url,
		});
	}
</aui:script>