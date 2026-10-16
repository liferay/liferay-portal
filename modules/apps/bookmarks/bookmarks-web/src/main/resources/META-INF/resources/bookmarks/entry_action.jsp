<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/bookmarks/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

BookmarksEntry entry = null;

boolean view = false;

if (row != null) {
	Object result = row.getObject();

	if (result instanceof AssetEntry) {
		AssetEntry assetEntry = (AssetEntry)result;

		entry = BookmarksEntryServiceUtil.getEntry(assetEntry.getClassPK());
	}
	else {
		entry = (BookmarksEntry)result;
	}
}
else {
	entry = (BookmarksEntry)request.getAttribute("info_panel.jsp-entry");

	view = true;
}
%>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message="actions"
	showWhenSingleIcon="<%= true %>"
>
	<c:if test="<%= BookmarksEntryPermission.contains(permissionChecker, entry, ActionKeys.UPDATE) %>">
		<liferay-ui:icon
			message="edit"
			url='<%=
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(request, themeDisplay.getScopeGroup(), BookmarksPortletKeys.BOOKMARKS_ADMIN, 0, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/bookmarks/edit_entry"
				).setRedirect(
					currentURL
				).setPortletResource(
					portletDisplay.getId()
				).setParameter(
					"entryId", entry.getEntryId()
				).buildString()
			%>'
		/>

		<portlet:renderURL var="moveURL">
			<portlet:param name="mvcRenderCommandName" value="/bookmarks/move_entry" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="rowIdsBookmarksEntry" value="<%= String.valueOf(entry.getEntryId()) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			message="move"
			url="<%= moveURL %>"
		/>
	</c:if>

	<c:if test="<%= BookmarksEntryPermission.contains(permissionChecker, entry, ActionKeys.PERMISSIONS) %>">
		<liferay-security:permissionsURL
			modelResource="<%= BookmarksEntry.class.getName() %>"
			modelResourceDescription="<%= entry.getName() %>"
			resourcePrimKey="<%= String.valueOf(entry.getEntryId()) %>"
			var="permissionsURL"
			windowState="<%= LiferayWindowState.POP_UP.toString() %>"
		/>

		<liferay-ui:icon
			message="permissions"
			method="get"
			url="<%= permissionsURL %>"
			useDialog="<%= true %>"
		/>
	</c:if>

	<c:if test="<%= BookmarksEntryPermission.contains(permissionChecker, entry, ActionKeys.SUBSCRIBE) && (bookmarksGroupServiceConfiguration.emailEntryAddedEnabled() || bookmarksGroupServiceConfiguration.emailEntryUpdatedEnabled()) %>">
		<c:choose>
			<c:when test="<%= SubscriptionLocalServiceUtil.isSubscribed(user.getCompanyId(), user.getUserId(), BookmarksEntry.class.getName(), entry.getEntryId()) %>">
				<portlet:actionURL name="/bookmarks/edit_entry" var="unsubscribeURL">
					<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UNSUBSCRIBE %>" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
					<portlet:param name="entryId" value="<%= String.valueOf(entry.getEntryId()) %>" />
				</portlet:actionURL>

				<liferay-ui:icon
					message="unsubscribe"
					url="<%= unsubscribeURL %>"
				/>
			</c:when>
			<c:otherwise>
				<portlet:actionURL name="/bookmarks/edit_entry" var="subscribeURL">
					<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.SUBSCRIBE %>" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
					<portlet:param name="entryId" value="<%= String.valueOf(entry.getEntryId()) %>" />
				</portlet:actionURL>

				<liferay-ui:icon
					message="subscribe"
					url="<%= subscribeURL %>"
				/>
			</c:otherwise>
		</c:choose>
	</c:if>

	<c:if test="<%= BookmarksEntryPermission.contains(permissionChecker, entry, ActionKeys.DELETE) %>">
		<portlet:renderURL var="redirectURL">
			<c:choose>
				<c:when test="<%= entry.getFolderId() == BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID %>">
					<portlet:param name="mvcRenderCommandName" value="/bookmarks/view" />
				</c:when>
				<c:otherwise>
					<portlet:param name="mvcRenderCommandName" value="/bookmarks/view_folder" />
					<portlet:param name="folderId" value="<%= String.valueOf(entry.getFolderId()) %>" />
				</c:otherwise>
			</c:choose>
		</portlet:renderURL>

		<portlet:actionURL name="/bookmarks/edit_entry" var="deleteURL">
			<portlet:param name="<%= Constants.CMD %>" value="<%= trashHelper.isTrashEnabled(scopeGroupId) ? Constants.MOVE_TO_TRASH : Constants.DELETE %>" />
			<portlet:param name="redirect" value="<%= view ? redirectURL : currentURL %>" />
			<portlet:param name="entryId" value="<%= String.valueOf(entry.getEntryId()) %>" />
		</portlet:actionURL>

		<liferay-ui:icon-delete
			trash="<%= trashHelper.isTrashEnabled(scopeGroupId) %>"
			url="<%= deleteURL %>"
		/>
	</c:if>

	<c:if test="<%= portletName.equals(BookmarksPortletKeys.BOOKMARKS_ADMIN) %>">
		<liferay-export-import-changeset:publish-entity-menu-item
			className="<%= BookmarksEntry.class.getName() %>"
			groupId="<%= entry.getGroupId() %>"
			uuid="<%= entry.getUuid() %>"
		/>
	</c:if>
</liferay-ui:icon-menu>