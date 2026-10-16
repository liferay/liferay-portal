<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/bookmarks/init.jsp" %>

<%
BookmarksFolder folder = (BookmarksFolder)request.getAttribute("info_panel.jsp-folder");

long folderId = BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID;

if (folder != null) {
	folderId = folder.getFolderId();
}

BookmarksEntry entry = (BookmarksEntry)request.getAttribute("info_panel.jsp-entry");
%>

<c:if test="<%= bookmarksGroupServiceConfiguration.emailEntryAddedEnabled() || bookmarksGroupServiceConfiguration.emailEntryUpdatedEnabled() %>">
	<c:choose>
		<c:when test="<%= entry != null %>">
			<c:if test="<%= BookmarksEntryPermission.contains(permissionChecker, entry, ActionKeys.SUBSCRIBE) %>">
				<c:choose>
					<c:when test="<%= SubscriptionLocalServiceUtil.isSubscribed(user.getCompanyId(), user.getUserId(), BookmarksEntry.class.getName(), entry.getEntryId()) %>">
						<portlet:actionURL name="/bookmarks/edit_entry" var="unsubscribeURL">
							<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UNSUBSCRIBE %>" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="entryId" value="<%= String.valueOf(entry.getEntryId()) %>" />
						</portlet:actionURL>

						<liferay-ui:icon
							icon="bell-off"
							linkCssClass="icon-monospaced"
							markupView="lexicon"
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
							icon="bell-on"
							linkCssClass="icon-monospaced"
							markupView="lexicon"
							message="subscribe"
							url="<%= subscribeURL %>"
						/>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:if test="<%= BookmarksFolderPermission.contains(permissionChecker, scopeGroupId, folderId, ActionKeys.SUBSCRIBE) %>">
				<c:choose>
					<c:when test="<%= (folder == null) ? SubscriptionLocalServiceUtil.isSubscribed(user.getCompanyId(), user.getUserId(), BookmarksFolder.class.getName(), scopeGroupId) : SubscriptionLocalServiceUtil.isSubscribed(user.getCompanyId(), user.getUserId(), BookmarksFolder.class.getName(), folder.getFolderId()) %>">
						<portlet:actionURL name="/bookmarks/edit_folder" var="unsubscribeURL">
							<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UNSUBSCRIBE %>" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" />
						</portlet:actionURL>

						<liferay-ui:icon
							icon="bell-off"
							linkCssClass="icon-monospaced"
							markupView="lexicon"
							message="unsubscribe"
							url="<%= unsubscribeURL %>"
						/>
					</c:when>
					<c:otherwise>
						<portlet:actionURL name="/bookmarks/edit_folder" var="subscribeURL">
							<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.SUBSCRIBE %>" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" />
						</portlet:actionURL>

						<liferay-ui:icon
							icon="bell-on"
							linkCssClass="icon-monospaced"
							markupView="lexicon"
							message="subscribe"
							url="<%= subscribeURL %>"
						/>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:otherwise>
	</c:choose>
</c:if>