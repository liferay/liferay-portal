<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/item/selector/init.jsp" %>

<%
WikiAttachmentItemSelectorViewDisplayContext wikiAttachmentItemSelectorViewDisplayContext = (WikiAttachmentItemSelectorViewDisplayContext)request.getAttribute(WikiItemSelectorWebKeys.WIKI_ATTACHMENT_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT);

int cur = ParamUtil.getInteger(request, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_CUR);
int delta = ParamUtil.getInteger(request, SearchContainer.DEFAULT_DELTA_PARAM, SearchContainer.DEFAULT_DELTA);

int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(cur, delta);

int start = startAndEnd[0];
int end = startAndEnd[1];

WikiPage wikiPage = wikiAttachmentItemSelectorViewDisplayContext.getWikiPage();

List<RepositoryEntry> portletFileEntries = new ArrayList<>();
int portletFileEntriesCount = 0;

String[] mimeTypes = wikiAttachmentItemSelectorViewDisplayContext.getMimeTypes();

if (wikiPage.getAttachmentsFolderId() != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
	if (wikiAttachmentItemSelectorViewDisplayContext.isSearch()) {
		SearchContext searchContext = SearchContextFactory.getInstance(request);

		if (ArrayUtil.isNotEmpty(mimeTypes)) {
			searchContext.setAttribute("mimeTypes", mimeTypes);
		}

		searchContext.setEnd(end);
		searchContext.setFolderIds(new long[] {wikiPage.getAttachmentsFolderId()});
		searchContext.setStart(start);

		Folder folder = PortletFileRepositoryUtil.getPortletFolder(wikiPage.getAttachmentsFolderId());

		Hits hits = PortletFileRepositoryUtil.searchPortletFileEntries(folder.getRepositoryId(), searchContext);

		portletFileEntriesCount = hits.getLength();

		for (Document doc : hits.getDocs()) {
			long fileEntryId = GetterUtil.getLong(doc.get(Field.ENTRY_CLASS_PK));

			FileEntry fileEntry = null;

			try {
				fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(fileEntryId);
			}
			catch (Exception e) {
				if (_log.isWarnEnabled()) {
					_log.warn("Documents and Media search index is stale and contains file entry {" + fileEntryId + "}");
				}

				continue;
			}

			portletFileEntries.add(fileEntry);
		}
	}
	else {
		if (ArrayUtil.isNotEmpty(mimeTypes)) {
			portletFileEntriesCount = wikiPage.getAttachmentsFileEntriesCount(mimeTypes);

			startAndEnd = SearchPaginationUtil.calculateStartAndEnd(start, end, portletFileEntriesCount);

			portletFileEntries.addAll(wikiPage.getAttachmentsFileEntries(mimeTypes, startAndEnd[0], startAndEnd[1], wikiAttachmentItemSelectorViewDisplayContext.getOrderByComparator()));
		}
		else {
			portletFileEntriesCount = wikiPage.getAttachmentsFileEntriesCount();

			startAndEnd = SearchPaginationUtil.calculateStartAndEnd(start, end, portletFileEntriesCount);

			portletFileEntries.addAll(wikiPage.getAttachmentsFileEntries(startAndEnd[0], startAndEnd[1], wikiAttachmentItemSelectorViewDisplayContext.getOrderByComparator()));
		}
	}
}
%>

<liferay-item-selector:repository-entry-browser
	allowedCreationMenuUIItemKeys="<%= wikiAttachmentItemSelectorViewDisplayContext.getAllowedCreationMenuUIItemKeys() %>"
	editImageURL="<%= wikiAttachmentItemSelectorViewDisplayContext.getEditImageURL(liferayPortletResponse) %>"
	emptyResultsMessage='<%= LanguageUtil.get(resourceBundle, "there-are-no-wiki-attachments") %>'
	extensions="<%= ListUtil.fromArray(mimeTypes) %>"
	itemSelectedEventName="<%= wikiAttachmentItemSelectorViewDisplayContext.getItemSelectedEventName() %>"
	itemSelectorReturnTypeResolver="<%= wikiAttachmentItemSelectorViewDisplayContext.getItemSelectorReturnTypeResolver() %>"
	maxFileSize="<%= wikiAttachmentItemSelectorViewDisplayContext.getWikiAttachmentMaxSize() %>"
	portletURL="<%= wikiAttachmentItemSelectorViewDisplayContext.getPortletURL(request, liferayPortletResponse) %>"
	repositoryEntries="<%= portletFileEntries %>"
	repositoryEntriesCount="<%= portletFileEntriesCount %>"
	showDragAndDropZone="<%= false %>"
	tabName="<%= wikiAttachmentItemSelectorViewDisplayContext.getTitle(locale) %>"
	uploadURL="<%= wikiAttachmentItemSelectorViewDisplayContext.getUploadURL(liferayPortletResponse) %>"
/>

<%!
private static final Log _log = LogFactoryUtil.getLog("com_liferay_wiki_web.item.selector.wiki_page_attachments_jsp");
%>