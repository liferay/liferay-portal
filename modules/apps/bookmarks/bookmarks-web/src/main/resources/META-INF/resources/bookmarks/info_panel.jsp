<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/bookmarks/init.jsp" %>

<%
List<BookmarksFolder> folders = (List<BookmarksFolder>)request.getAttribute(BookmarksWebKeys.BOOKMARKS_FOLDERS);
List<BookmarksEntry> entries = (List<BookmarksEntry>)request.getAttribute(BookmarksWebKeys.BOOKMARKS_ENTRIES);

if (ListUtil.isEmpty(folders) && ListUtil.isEmpty(entries)) {
	long folderId = GetterUtil.getLong((String)request.getAttribute("view.jsp-folderId"), ParamUtil.getLong(request, "folderId"));

	folders = new ArrayList<BookmarksFolder>();

	if (folderId != BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
		folders.add(BookmarksFolderLocalServiceUtil.getFolder(folderId));
	}
	else {
		folders.add(null);
	}
}
%>

<c:choose>
	<c:when test="<%= ListUtil.isEmpty(entries) && ListUtil.isNotEmpty(folders) && (folders.size() == 1) %>">

		<%
		BookmarksFolder folder = folders.get(0);

		request.setAttribute("info_panel.jsp-folder", folder);
		%>

		<div class="sidebar-header">
			<div class="autofit-row sidebar-section">
				<div class="autofit-col autofit-col-expand">
					<div class="component-title"><%= (folder != null) ? HtmlUtil.escape(folder.getName()) : LanguageUtil.get(request, "home") %></div>

					<div class="component-subtitle">
						<liferay-ui:message key="folder" />
					</div>
				</div>

				<div class="autofit-col">
					<ul class="autofit-padded-no-gutters autofit-row">
						<li class="autofit-col">
							<liferay-util:include page="/bookmarks/subscribe.jsp" servletContext="<%= application %>" />
						</li>
						<li class="autofit-col">
							<liferay-util:include page="/bookmarks/folder_action.jsp" servletContext="<%= application %>" />
						</li>
					</ul>
				</div>
			</div>
		</div>

		<clay:navigation-bar
			navigationItems='<%=
				new JSPNavigationItemList(pageContext) {
					{
						add(
							navigationItem -> {
								navigationItem.setActive(true);
								navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "details"));
							});
					}
				}
			%>'
		/>

		<div class="sidebar-body">
			<dl class="sidebar-dl sidebar-section">
				<dt class="sidebar-dt">
					<liferay-ui:message key="num-of-items" />
				</dt>

				<%
				long folderId = BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID;

				if (folder != null) {
					folderId = folder.getFolderId();
				}
				%>

				<dd class="sidebar-dd">
					<%= BookmarksFolderServiceUtil.getFoldersAndEntriesCount(scopeGroupId, folderId, WorkflowConstants.STATUS_APPROVED) %>
				</dd>

				<c:if test="<%= folder != null %>">
					<dt class="sidebar-dt">
						<liferay-ui:message key="created" />
					</dt>
					<dd class="sidebar-dd">
						<%= HtmlUtil.escape(folder.getUserName()) %>
					</dd>
				</c:if>
			</dl>
		</div>
	</c:when>
	<c:when test="<%= ListUtil.isEmpty(folders) && ListUtil.isNotEmpty(entries) && (entries.size() == 1) %>">

		<%
		BookmarksEntry entry = entries.get(0);

		request.setAttribute("info_panel.jsp-entry", entry);
		%>

		<div class="sidebar-header">
			<div class="autofit-row sidebar-section">
				<div class="autofit-col autofit-col-expand">
					<div class="component-title"><%= HtmlUtil.escape(entry.getName()) %></div>

					<div class="h5">
						<liferay-ui:message key="entry" />
					</div>
				</div>

				<div class="autofit-col">
					<ul class="autofit-padded-no-gutters autofit-row">
						<li class="autofit-col">
							<liferay-util:include page="/bookmarks/subscribe.jsp" servletContext="<%= application %>" />
						</li>
						<li class="autofit-col">
							<liferay-util:include page="/bookmarks/entry_action.jsp" servletContext="<%= application %>" />
						</li>
					</ul>
				</div>
			</div>
		</div>

		<clay:navigation-bar
			navigationItems='<%=
				new JSPNavigationItemList(pageContext) {
					{
						add(
							navigationItem -> {
								navigationItem.setActive(true);
								navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "details"));
							});
					}
				}
			%>'
		/>

		<div class="sidebar-body">
			<dl class="sidebar-dl sidebar-section">
				<dt class="sidebar-dt">
					<liferay-ui:message key="created" />
				</dt>
				<dd class="sidebar-dd">
					<%= HtmlUtil.escape(entry.getUserName()) %>
				</dd>

				<c:if test="<%= Validator.isNotNull(entry.getDescription()) %>">
					<dt class="sidebar-dt">
						<liferay-ui:message key="description" />
					</dt>
					<dd class="sidebar-dd">
						<%= HtmlUtil.escape(entry.getDescription()) %>
					</dd>
				</c:if>

				<dt class="sidebar-dt">
					<liferay-ui:message key="url" />
				</dt>
				<dd class="sidebar-dd">
					<%= HtmlUtil.escape(entry.getUrl()) %>
				</dd>
				<dt class="sidebar-dt">
					<liferay-ui:message key="visits" />
				</dt>
				<dd class="sidebar-dd">
					<%= entry.getVisits() %>
				</dd>
			</dl>

			<div class="lfr-asset-categories sidebar-section">
				<liferay-asset:asset-categories-summary
					className="<%= BookmarksEntry.class.getName() %>"
					classPK="<%= entry.getEntryId() %>"
				/>
			</div>

			<div class="lfr-asset-tags sidebar-section">
				<liferay-asset:asset-tags-summary
					className="<%= BookmarksEntry.class.getName() %>"
					classPK="<%= entry.getEntryId() %>"
					message="tags"
				/>
			</div>

			<liferay-ratings:ratings
				className="<%= BookmarksEntry.class.getName() %>"
				classPK="<%= entry.getEntryId() %>"
				inTrash="<%= entry.isInTrash() %>"
			/>

			<liferay-expando:custom-attributes-available
				className="<%= BookmarksEntry.class.getName() %>"
			>
				<liferay-expando:custom-attribute-list
					className="<%= BookmarksEntry.class.getName() %>"
					classPK="<%= entry.getEntryId() %>"
					editable="<%= false %>"
					label="<%= true %>"
				/>
			</liferay-expando:custom-attributes-available>

			<c:if test="<%= bookmarksGroupServiceConfiguration.enableRelatedAssets() %>">

				<%
				AssetEntry layoutAssetEntry = AssetEntryLocalServiceUtil.getEntry(BookmarksEntry.class.getName(), entry.getEntryId());
				%>

				<div class="entry-links">
					<liferay-asset:asset-links
						assetEntryId="<%= layoutAssetEntry.getEntryId() %>"
					/>
				</div>
			</c:if>
		</div>
	</c:when>
	<c:otherwise>
		<div class="sidebar-header">
			<div class="autofit-row sidebar-section">
				<div class="autofit-col autofit-col-expand">
					<div class="component-title"><liferay-ui:message arguments="<%= folders.size() + entries.size() %>" key="x-items-are-selected" /></div>
				</div>
			</div>
		</div>

		<clay:navigation-bar
			navigationItems='<%=
				new JSPNavigationItemList(pageContext) {
					{
						add(
							navigationItem -> {
								navigationItem.setActive(true);
								navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "details"));
							});
					}
				}
			%>'
		/>

		<div class="sidebar-body">
			<div class="h5"><liferay-ui:message arguments="<%= folders.size() + entries.size() %>" key="x-items-are-selected" /></div>
		</div>
	</c:otherwise>
</c:choose>