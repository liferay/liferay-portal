<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/bookmarks/init.jsp" %>

<%
BookmarksFolder folder = (BookmarksFolder)request.getAttribute(BookmarksWebKeys.BOOKMARKS_FOLDER);

long folderId = BeanParamUtil.getLong(folder, request, "folderId", BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID);

String eventName = ParamUtil.getString(request, "eventName", liferayPortletResponse.getNamespace() + "selectFolder");

String folderName = LanguageUtil.get(request, "home");

if (folder != null) {
	folderName = folder.getName();

	BookmarksUtil.addPortletBreadcrumbEntries(folder, request, renderResponse);
}
%>

<clay:container-fluid>
	<aui:form method="post" name="selectFolderFm">
		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, false, true) %>"
		/>

		<br />

		<liferay-ui:search-container
			iteratorURL='<%=
				PortletURLBuilder.createRenderURL(
					renderResponse
				).setMVCRenderCommandName(
					"/bookmarks/select_folder"
				).setParameter(
					"eventName", eventName
				).setParameter(
					"folderId", folderId
				).buildPortletURL()
			%>'
			total="<%= BookmarksFolderServiceUtil.getFoldersCount(scopeGroupId, folderId) %>"
		>
			<liferay-ui:search-container-results
				results="<%= BookmarksFolderServiceUtil.getFolders(scopeGroupId, folderId, searchContainer.getStart(), searchContainer.getEnd()) %>"
			/>

			<liferay-ui:search-container-row
				className="com.liferay.bookmarks.model.BookmarksFolder"
				modelVar="curFolder"
			>
				<portlet:renderURL var="viewFolderURL">
					<portlet:param name="mvcRenderCommandName" value="/bookmarks/select_folder" />
					<portlet:param name="folderId" value="<%= String.valueOf(curFolder.getFolderId()) %>" />
				</portlet:renderURL>

				<liferay-ui:search-container-column-text
					name="folder"
				>

					<%
					AssetRendererFactory<BookmarksFolder> assetRendererFactory = AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(BookmarksFolder.class);

					AssetRenderer<BookmarksFolder> assetRenderer = assetRendererFactory.getAssetRenderer(curFolder.getFolderId());
					%>

					<liferay-ui:icon
						icon="<%= assetRenderer.getIconCssClass() %>"
						label="<%= true %>"
						localizeMessage="<%= false %>"
						markupView="lexicon"
						message="<%= HtmlUtil.escape(curFolder.getName()) %>"
						url="<%= viewFolderURL %>"
					/>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					href="<%= viewFolderURL %>"
					name="num-of-folders"
					value="<%= String.valueOf(BookmarksFolderServiceUtil.getFoldersCount(scopeGroupId, curFolder.getFolderId(), WorkflowConstants.STATUS_APPROVED)) %>"
				/>

				<liferay-ui:search-container-column-text
					href="<%= viewFolderURL %>"
					name="num-of-entries"
					value="<%= String.valueOf(BookmarksEntryServiceUtil.getFoldersEntriesCount(scopeGroupId, Arrays.asList(curFolder.getFolderId()))) %>"
				/>

				<liferay-ui:search-container-column-text>
					<aui:button
						cssClass="selector-button"
						data='<%=
							HashMapBuilder.<String, Object>put(
								"resourceid", curFolder.getFolderId()
							).put(
								"resourcename", curFolder.getName()
							).build()
						%>'
						value="choose"
					/>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<aui:button-row>
				<c:if test="<%= BookmarksFolderPermission.contains(permissionChecker, scopeGroupId, folderId, ActionKeys.ADD_FOLDER) %>">
					<portlet:renderURL var="editFolderURL">
						<portlet:param name="mvcRenderCommandName" value="/bookmarks/edit_folder" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
						<portlet:param name="parentFolderId" value="<%= String.valueOf(folderId) %>" />
					</portlet:renderURL>

					<aui:button href="<%= editFolderURL %>" value='<%= (folder == null) ? "add-folder" : "add-subfolder" %>' />
				</c:if>

				<aui:button
					cssClass="selector-button"
					data='<%=
						HashMapBuilder.<String, Object>put(
							"resourceid", folderId
						).put(
							"resourcename", folderName
						).build()
					%>'
					value="choose-this-folder"
				/>
			</aui:button-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>