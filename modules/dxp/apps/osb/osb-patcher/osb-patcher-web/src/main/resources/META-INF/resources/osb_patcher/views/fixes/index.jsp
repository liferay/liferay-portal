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
		<portlet:renderURL var="viewPatcherFixPopUpURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="fixes" />
			<portlet:param name="action" value="view" />
			<portlet:param name="id" value="<%= patcherFix.patcherFixId %>" />
		</portlet:renderURL>

		<c:set value='<%= LanguageUtil.get(request, "view-fix") %>' var="viewPatcherFixPopUpTitle" />

		<c:set value='<%= "javascript:Liferay.Patcher.openWindow("" + viewPatcherFixPopUpURL %>", "<%= viewPatcherFixPopUpTitle + "", true, 1000)" %>' var="viewPatcherFixPopUpURL" />

		<liferay-ui:search-container-column-text
			cssClass="osb-patcher-search-container-column-text-icon"
		>
			<liferay-ui:icon
				image='<%= patcherFix.obsolete ? "../common/activate" : StringPool.BLANK %>'
				message="this-fix-is-obsolete"
				url="<%= viewPatcherFixPopUpURL %>"
			/>
		</liferay-ui:search-container-column-text>

		<portlet:renderURL var="viewPatcherFixURL">
			<portlet:param name="controller" value="fixes" />
			<portlet:param name="action" value="view" />
			<portlet:param name="id" value="<%= patcherFix.patcherFixId %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			href="<%= viewPatcherFixURL %>"
			name="fix-id"
			property="patcherFixId"
		/>

		<liferay-ui:search-container-column-text
			name="content"
		>
			<c:set value="<%= StringUtil.split(patcherFix.getName()) %>" var="tokens" />

			<c:forEach items="<%= tokens %>" var="token" varStatus="tokenStatus">
				<c:choose>
					<c:when test="<%= PatcherFixPackUtil.containsPatcherFixPackName(token) %>">
						<c:set value="<%= PatcherFixPackUtil.getPatcherFixPack(token, patcherFix.getPatcherProjectVersionId()) %>" var="patcherFixPack" />

						<portlet:renderURL var="viewPatcherFixPackURL">
							<portlet:param name="controller" value="fix_packs" />
							<portlet:param name="action" value="view" />
							<portlet:param name="id" value="<%= patcherFixPack.patcherFixPackId %>" />
						</portlet:renderURL>

						<a class="nobr" href="<%= viewPatcherFixPackURL %>"><%= token %></a>,
					</c:when>
					<c:otherwise>
						<a class="nobr" href="<%= patcherConfiguration.jiraURL() %>/<%= token %>" target="_blank"><%= token %></a>,
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="version"
			property="keyVersion"
		/>

		<c:set value="<%= PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherFix.getPatcherProjectVersionId()) %>" var="patcherProjectVersion" />

		<liferay-ui:search-container-column-text
			name="product-version"
			value="<%= PatcherProductVersionUtil.fetchPatcherProductVersionName(patcherProjectVersion.getPatcherProductVersionId()) %>"
		/>

		<liferay-ui:search-container-column-text
			name="project-version"
			value="<%= patcherProjectVersion.name %>"
		/>

		<liferay-ui:search-container-column-text
			href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()) %>"
			name="git-hash"
			target="_blank"
			value="<%= fn:substring(patcherFix.gitHash, 0, 10) %>"
		/>

		<liferay-ui:search-container-column-text
			name="patcher-status"
			value="<%= LanguageUtil.get(request, WorkflowConstantsMethods.getStatusLabel(patcherFix.getStatus())) %>"
		/>

		<portlet:renderURL var="editPatcherFixCommentsFieldURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="controller" value="fixes" />
			<portlet:param name="action" value="editCommentsField" />
			<portlet:param name="id" value="<%= patcherFix.patcherFixId %>" />
		</portlet:renderURL>

		<c:set value='<%= UnicodeLanguageUtil.format(request, "edit-engineer-comments-for-fix-id-x", patcherFix.patcherFixId) %>' var="editPatcherFixCommentsFieldURLTitle" />

		<c:set value='<%= "javascript:Liferay.Patcher.openWindow('" + editPatcherFixCommentsFieldURL %>', '<%= editPatcherFixCommentsFieldURLTitle + "', true, 800)" %>' var="editPatcherFixCommentsFieldURL" />

		<c:set value="<%= StringUtil.shorten(patcherFix.comments, 75) %>" var="shortenedPatcherFixComments" />

		<liferay-ui:search-container-column-text
			name="engineer-comments"
		>
			<c:choose>
				<c:when test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherFix.userId) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<a href="<%= editPatcherFixCommentsFieldURL %>">
						<%= shortenedPatcherFixComments %>
					</a>
				</c:when>
				<c:otherwise>
					<%= shortenedPatcherFixComments %>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="jenkins"
		>
			<c:set value="<%= JenkinsUtil.getJenkinsResults(patcherFix) %>" var="jenkinsResults" />

			<c:forEach items="<%= jenkinsResults %>" var="jenkinsResult">
				<clay:link
					cssClass="nobr"
					href="<%= jenkinsResult.statusURL %>"
					target="_blank"
					title="<%= jenkinsResult.jobName %>"
				/>
			</c:forEach>
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
				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT, patcherFix.userId) && patcherFix.latestFix && (patcherFix.type != PatcherFixConstants.TYPE_FIX_PACK) %>">
					<portlet:renderURL var="editPatcherFixURL">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="edit" />
						<portlet:param name="id" value="<%= patcherFix.patcherFixId %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="<%= editPatcherFixURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherFix.userId) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<liferay-ui:icon
						image="edit"
						message="edit-engineer-comments"
						method="get"
						url="<%= editPatcherFixCommentsFieldURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EDIT_FIX_PACK_FIELDS, patcherFix.userId) && (patcherBuild.type != PatcherBuildConstants.TYPE_FIX_PACK) %>">
					<portlet:renderURL var="editPatcherFixFixPackFieldsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="editFixPackFields" />
						<portlet:param name="id" value="<%= patcherFix.patcherFixId %>" />
					</portlet:renderURL>

					<c:set value='<%= UnicodeLanguageUtil.get(request, "edit-fix-packs") %>' var="editPatcherFixFixPackFieldsURLTitle" />

					<c:set value='<%= "javascript:Liferay.Patcher.openWindow('" + editPatcherFixFixPackFieldsURL %>', '<%= editPatcherFixFixPackFieldsURLTitle + "', true, 800)" %>' var="editPatcherFixFixPackFieldsURL" />

					<liferay-ui:icon
						image="edit"
						message="edit-fix-packs"
						method="get"
						url="<%= editPatcherFixFixPackFieldsURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.BUILDS, patcherFix.userId) %>">
					<portlet:renderURL var="viewPatcherFixPatcherBuildsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="builds" />
						<portlet:param name="id" value="<%= patcherFix.patcherFixId %>" />
					</portlet:renderURL>

					<c:set value='<%= UnicodeLanguageUtil.format(request, "view-builds-for-fix-id-x", patcherFix.patcherFixId) %>' var="viewPatcherBuildsURLTitle" />

					<c:set value='<%= "javascript:Liferay.Patcher.openWindow('" + viewPatcherFixPatcherBuildsURL %>', '<%= viewPatcherBuildsURLTitle + "', true, 1000);" %>' var="viewPatcherFixPatcherBuildsURL" />

					<liferay-ui:icon
						image="view"
						message="view-builds"
						method="get"
						url="<%= viewPatcherFixPatcherBuildsURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.FIXES, patcherFix.userId) %>">
					<portlet:renderURL var="viewPatcherFixesPopUpURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="fixes" />
						<portlet:param name="id" value="<%= patcherFix.patcherFixId %>" />
					</portlet:renderURL>

					<c:set value='<%= UnicodeLanguageUtil.format(request, "view-fixes-for-fix-id-x", patcherFix.patcherFixId) %>' var="viewPatcherFixesPopUpURLTitle" />

					<c:set value='<%= "javascript:Liferay.Patcher.openWindow('" + viewPatcherFixesPopUpURL %>', '<%= viewPatcherFixesPopUpURLTitle + "', true, 1000);" %>' var="viewPatcherFixesPopUpURL" />

					<liferay-ui:icon
						image="view"
						message="view-fixes"
						method="get"
						url="<%= viewPatcherFixesPopUpURL %>"
					/>
				</c:if>

				<c:if test="<%= PatcherPermission.contains(permissionChecker, patcherFix, PatcherActionKeys.EXCLUDE, patcherFix.userId) && (patcherFix.type != PatcherFixConstants.TYPE_EXCLUDED) && (patcherFix.type != PatcherFixConstants.TYPE_FIX_PACK) %>">
					<portlet:actionURL var="excludePatcherFixURL">
						<portlet:param name="controller" value="fixes" />
						<portlet:param name="action" value="exclude" />
						<portlet:param name="id" value="<%= patcherFix.patcherFixId %>" />
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
	Liferay.provide(
		window,
		'<portlet:namespace />productVersionOnChange',
		function(productVersionId) {
			var namespace = '<portlet:namespace />';

			window.location.href = Liferay.Patcher.updateProductVersionId('${alloySearchResult.portletURL}', productVersionId, namespace);
		},
		['aui-base']
	);
</aui:script>