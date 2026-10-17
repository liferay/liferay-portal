<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/bookmarks/init.jsp" %>

<%
BookmarksFolder folder = (BookmarksFolder)request.getAttribute(BookmarksWebKeys.BOOKMARKS_FOLDER);

long folderId = BeanParamUtil.getLong(folder, request, "folderId", rootFolderId);

String keywords = ParamUtil.getString(request, "keywords");

boolean portletTitleBasedNavigation = GetterUtil.getBoolean(portletConfig.getInitParameter("portlet-title-based-navigation"));

if (Validator.isNotNull(keywords) && portletTitleBasedNavigation) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(ParamUtil.getString(request, "redirect"));

	renderResponse.setTitle(LanguageUtil.get(resourceBundle, "search"));
}

boolean defaultFolderView = false;

if ((folder == null) && (folderId != BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {
	defaultFolderView = true;
}

if (defaultFolderView) {
	try {
		folder = BookmarksFolderServiceUtil.getFolder(folderId);
	}
	catch (NoSuchFolderException nsfe) {
		folderId = BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	}
}

BookmarksDisplayContext bookmarksDisplayContext = new BookmarksDisplayContext(request, liferayPortletRequest, liferayPortletResponse, folderId);

request.setAttribute("view.jsp-folderId", String.valueOf(folderId));

request.setAttribute("view.jsp-displayStyle", bookmarksDisplayContext.getDisplayStyle());

request.setAttribute("view.jsp-bookmarksSearchContainer", bookmarksDisplayContext.getSearchContainer());

BookmarksUtil.addPortletBreadcrumbEntries(folder, request, renderResponse);
%>

<liferay-ui:success key='<%= portletDisplay.getId() + "requestProcessed" %>' message="your-request-completed-successfully" />

<portlet:actionURL name="/bookmarks/edit_entry" var="restoreTrashEntriesURL">
	<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.RESTORE %>" />
</portlet:actionURL>

<liferay-trash:undo
	portletURL="<%= restoreTrashEntriesURL %>"
/>

<liferay-util:include page="/bookmarks/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="searchContainerId" value="entries" />
</liferay-util:include>

<div class="closed sidenav-container sidenav-right" id="<portlet:namespace />infoPanelId">
	<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/bookmarks/info_panel" var="sidebarPanelURL">
		<portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" />
	</liferay-portlet:resourceURL>

	<liferay-frontend:sidebar-panel
		resourceURL="<%= sidebarPanelURL %>"
		searchContainerId="entries"
	>
		<liferay-util:include page="/bookmarks/info_panel.jsp" servletContext="<%= application %>" />
	</liferay-frontend:sidebar-panel>

	<clay:container-fluid
		cssClass="container-view sidenav-content"
		size="xxxl"
	>
		<div class="bookmarks-breadcrumb" id="<portlet:namespace />breadcrumbContainer">
			<c:if test="<%= !bookmarksDisplayContext.isNavigationRecent() && !bookmarksDisplayContext.isNavigationMine() %>">
				<liferay-site-navigation:breadcrumb
					breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, false, true) %>"
				/>
			</c:if>
		</div>

		<liferay-portlet:actionURL varImpl="editEntryURL">
			<portlet:param name="mvcRenderCommandName" value="/bookmarks/edit_entry" />
		</liferay-portlet:actionURL>

		<aui:form action="<%= editEntryURL %>" method="get" name="fm">
			<aui:input name="<%= Constants.CMD %>" type="hidden" />
			<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
			<aui:input name="newFolderId" type="hidden" />

			<liferay-util:include page="/bookmarks/view_entries.jsp" servletContext="<%= application %>">
				<liferay-util:param name="searchContainerId" value="entries" />
			</liferay-util:include>
		</aui:form>
	</clay:container-fluid>
</div>

<%
if (bookmarksDisplayContext.isNavigationHome() && !defaultFolderView && (folder != null) && (portletName.equals(BookmarksPortletKeys.BOOKMARKS) || portletName.equals(BookmarksPortletKeys.BOOKMARKS_ADMIN))) {
	PortalUtil.setPageSubtitle(folder.getName(), request);
	PortalUtil.setPageDescription(folder.getDescription(), request);
}
else {
	if (!layout.isTypeControlPanel()) {
		PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, bookmarksDisplayContext.getNavigation()), currentURL);
	}

	PortalUtil.setPageSubtitle(LanguageUtil.get(request, StringUtil.replace(bookmarksDisplayContext.getNavigation(), CharPool.UNDERLINE, CharPool.DASH)), request);
}
%>

<portlet:actionURL name="/bookmarks/edit_entry" var="editEntryURL" />

<portlet:renderURL var="moveEntryURL">
	<portlet:param name="mvcRenderCommandName" value="/bookmarks/move_entry" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:renderURL>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"editEntryURL", editEntryURL
		).put(
			"moveEntryURL", moveEntryURL
		).put(
			"searchContainerId", "entries"
		).build()
	%>'
	module="{BookmarksDragAndDrop} from bookmarks-web"
/>