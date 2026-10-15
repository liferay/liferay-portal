<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
KBAttachmentItemSelectorViewDisplayContext kbAttachmentItemSelectorViewDisplayContext = (KBAttachmentItemSelectorViewDisplayContext)request.getAttribute(KBItemSelectorWebKeys.KB_ATTACHMENT_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT);

int cur = ParamUtil.getInteger(request, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_CUR);
int delta = ParamUtil.getInteger(request, SearchContainer.DEFAULT_DELTA_PARAM, SearchContainer.DEFAULT_DELTA);

int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(cur, delta);

int start = startAndEnd[0];
int end = startAndEnd[1];

long folderId = kbAttachmentItemSelectorViewDisplayContext.getAttachmentsFolderId();

List<RepositoryEntry> portletFileEntries = new ArrayList<>();

int portletFileEntriesCount = 0;

if (kbAttachmentItemSelectorViewDisplayContext.isSearch()) {
	SearchContext searchContext = SearchContextFactory.getInstance(request);

	searchContext.setEnd(end);
	searchContext.setFolderIds(new long[] {folderId});
	searchContext.setStart(start);

	Folder folder = PortletFileRepositoryUtil.getPortletFolder(folderId);

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
	portletFileEntriesCount = PortletFileRepositoryUtil.getPortletFileEntriesCount(scopeGroupId, folderId, WorkflowConstants.STATUS_APPROVED);

	startAndEnd = SearchPaginationUtil.calculateStartAndEnd(start, end, portletFileEntriesCount);

	portletFileEntries.addAll(PortletFileRepositoryUtil.getPortletFileEntries(scopeGroupId, folderId, WorkflowConstants.STATUS_APPROVED, startAndEnd[0], startAndEnd[1], kbAttachmentItemSelectorViewDisplayContext.getOrderByComparator()));
}
%>

<liferay-item-selector:repository-entry-browser
	emptyResultsMessage='<%= LanguageUtil.get(resourceBundle, "there-are-no-attachments") %>'
	itemSelectedEventName="<%= kbAttachmentItemSelectorViewDisplayContext.getItemSelectedEventName() %>"
	itemSelectorReturnTypeResolver="<%= kbAttachmentItemSelectorViewDisplayContext.getItemSelectorReturnTypeResolver() %>"
	portletURL="<%= kbAttachmentItemSelectorViewDisplayContext.getPortletURL(request, liferayPortletResponse) %>"
	repositoryEntries="<%= portletFileEntries %>"
	repositoryEntriesCount="<%= portletFileEntriesCount %>"
	showDragAndDropZone="<%= false %>"
	tabName="<%= kbAttachmentItemSelectorViewDisplayContext.getTitle(locale) %>"
	uploadURL="<%= kbAttachmentItemSelectorViewDisplayContext.getUploadURL(liferayPortletResponse) %>"
/>

<%!
private static final Log _log = LogFactoryUtil.getLog("com_liferay_knowledge_base_item_selector_web.kb_article_attachments_jsp");
%>