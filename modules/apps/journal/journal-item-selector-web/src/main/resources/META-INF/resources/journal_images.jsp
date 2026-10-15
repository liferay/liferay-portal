<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
JournalItemSelectorViewDisplayContext journalItemSelectorViewDisplayContext = (JournalItemSelectorViewDisplayContext)request.getAttribute(JournalItemSelectorWebKeys.JOURNAL_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT);

int cur = ParamUtil.getInteger(request, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_CUR);
int delta = ParamUtil.getInteger(request, SearchContainer.DEFAULT_DELTA_PARAM, SearchContainer.DEFAULT_DELTA);

int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(cur, delta);

int start = startAndEnd[0];
int end = startAndEnd[1];

JournalArticle journalArticle = journalItemSelectorViewDisplayContext.getJournalArticle();

List<RepositoryEntry> portletFileEntries = new ArrayList<>();
int portletFileEntriesCount = 0;

if (journalArticle != null) {
	portletFileEntriesCount = journalArticle.getImagesFileEntriesCount();

	startAndEnd = SearchPaginationUtil.calculateStartAndEnd(start, end, portletFileEntriesCount);

	portletFileEntries.addAll(journalArticle.getImagesFileEntries(startAndEnd[0], startAndEnd[1], (OrderByComparator<FileEntry>)journalItemSelectorViewDisplayContext.getOrderByComparator()));
}
%>

<liferay-item-selector:repository-entry-browser
	allowedCreationMenuUIItemKeys="<%= Collections.emptySet() %>"
	editImageURL="<%= journalItemSelectorViewDisplayContext.getEditImageURL(liferayPortletResponse) %>"
	emptyResultsMessage='<%= LanguageUtil.get(resourceBundle, "there-are-no-journal-images") %>'
	itemSelectedEventName="<%= journalItemSelectorViewDisplayContext.getItemSelectedEventName() %>"
	itemSelectorReturnTypeResolver="<%= journalItemSelectorViewDisplayContext.getItemSelectorReturnTypeResolver() %>"
	maxFileSize="<%= DLValidatorUtil.getMaxAllowableSize(themeDisplay.getScopeGroupId(), null) %>"
	mimeTypeRestriction="<%= journalItemSelectorViewDisplayContext.getMimeTypeRestriction() %>"
	portletURL="<%= journalItemSelectorViewDisplayContext.getPortletURL(request, liferayPortletResponse) %>"
	repositoryEntries="<%= portletFileEntries %>"
	repositoryEntriesCount="<%= portletFileEntriesCount %>"
	showSearch="<%= false %>"
	tabName="<%= journalItemSelectorViewDisplayContext.getTitle(locale) %>"
	uploadURL="<%= journalItemSelectorViewDisplayContext.getUploadURL(liferayPortletResponse) %>"
/>