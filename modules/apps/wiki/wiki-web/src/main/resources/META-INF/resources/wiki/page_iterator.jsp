<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/wiki/init.jsp" %>

<%
WikiNode node = (WikiNode)request.getAttribute(WikiWebKeys.WIKI_NODE);
WikiPage wikiPage = (WikiPage)request.getAttribute(WikiWebKeys.WIKI_PAGE);

String navigation = ParamUtil.getString(request, "navigation", "all-pages");

long categoryId = ParamUtil.getLong(request, "categoryId");
String tagName = ParamUtil.getString(request, "tag");

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setParameter(
	"nodeName", node.getName()
).buildPortletURL();

if (wikiPage != null) {
	portletURL.setParameter("title", wikiPage.getTitle());
}

if (navigation.equals("all-pages")) {
	portletURL.setParameter("mvcRenderCommandName", "/wiki/view_pages");

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "all-pages"), portletURL.toString());
}
else if (navigation.equals("categorized-pages")) {
	portletURL.setParameter("mvcRenderCommandName", "/wiki/view_categorized_pages");
	portletURL.setParameter("categoryId", String.valueOf(categoryId));
}
else if (navigation.equals("draft-pages")) {
	portletURL.setParameter("mvcRenderCommandName", "/wiki/view_draft_pages");

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "draft-pages"), portletURL.toString());
}
else if (navigation.equals("history")) {
	if (wikiPage != null) {
		portletURL.setParameter("mvcRenderCommandName", "/wiki/view");

		PortalUtil.addPortletBreadcrumbEntry(request, wikiPage.getTitle(), portletURL.toString());
	}

	PortalUtil.addPortletBreadcrumbEntry(
		request, LanguageUtil.get(request, "history[record]"),
		PortletURLBuilder.create(
			PortletURLUtil.clone(portletURL, renderResponse)
		).setMVCRenderCommandName(
			"/wiki/view_page_activities"
		).buildString());
}
else if (navigation.equals("incoming-links")) {
	if (wikiPage != null) {
		portletURL.setParameter("mvcRenderCommandName", "/wiki/view");

		PortalUtil.addPortletBreadcrumbEntry(request, wikiPage.getTitle(), portletURL.toString());
	}

	portletURL.setParameter("mvcRenderCommandName", "/wiki/view_page_incoming_links");

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "incoming-links"), portletURL.toString());
}
else if (navigation.equals("orphan-pages")) {
	portletURL.setParameter("mvcRenderCommandName", "/wiki/view_orphan_pages");

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "orphan-pages"), portletURL.toString());
}
else if (navigation.equals("outgoing-links")) {
	if (wikiPage != null) {
		portletURL.setParameter("mvcRenderCommandName", "/wiki/view");

		PortalUtil.addPortletBreadcrumbEntry(request, wikiPage.getTitle(), portletURL.toString());
	}

	portletURL.setParameter("mvcRenderCommandName", "/wiki/view_page_outgoing_links");

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "outgoing-links"), portletURL.toString());
}
else if (navigation.equals("recent-changes")) {
	portletURL.setParameter("mvcRenderCommandName", "/wiki/view_recent_changes");

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "recent-changes"), portletURL.toString());
}
else if (navigation.equals("tagged-pages")) {
	portletURL.setParameter("mvcRenderCommandName", "/wiki/view_tagged_pages");
	portletURL.setParameter("tag", tagName);
}

List<String> headerNames = new ArrayList<String>();

headerNames.add("page");
headerNames.add("status");
headerNames.add("revision");
headerNames.add("user");
headerNames.add("date");

if (navigation.equals("history") || navigation.equals("recent-changes")) {
	headerNames.add("summary");
}

if (navigation.equals("all-pages") || navigation.equals("categorized-pages") || navigation.equals("draft-pages") || navigation.equals("history") || navigation.equals("orphan-pages") || navigation.equals("recent-changes") || navigation.equals("tagged-pages")) {
	headerNames.add(StringPool.BLANK);
}

WikiListPagesDisplayContext wikiListPagesDisplayContext = new WikiListPagesDisplayContext(request, (TrashHelper)request.getAttribute(TrashWebKeys.TRASH_HELPER), node);

String orderByCol = ParamUtil.getString(request, "orderByCol");
String orderByType = ParamUtil.getString(request, "orderByType");

SearchContainer<WikiPage> searchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, currentURLObj, headerNames, wikiListPagesDisplayContext.getEmptyResultsMessage());

Map<String, String> orderableHeaders = new HashMap<>();

if (navigation.equals("all-pages") || navigation.equals("categorized-pages") || navigation.equals("tagged-pages")) {
	orderableHeaders.put("date", "modifiedDate");
	orderableHeaders.put("page", "title");
}

searchContainer.setOrderableHeaders(orderableHeaders);
searchContainer.setOrderByCol(orderByCol);
searchContainer.setOrderByType(orderByType);

if (navigation.equals("history")) {
	RowChecker rowChecker = new RowChecker(renderResponse);

	rowChecker.setAllRowIds(null);

	searchContainer.setRowChecker(rowChecker);
}

wikiListPagesDisplayContext.populateResultsAndTotal(searchContainer);

List<WikiPage> pages = searchContainer.getResults();

List<com.liferay.portal.kernel.dao.search.ResultRow> resultRows = searchContainer.getResultRows();

for (int i = 0; i < pages.size(); i++) {
	WikiPage curWikiPage = pages.get(i);

	ResultRow row = new ResultRow(curWikiPage, String.valueOf(curWikiPage.getVersion()), i);

	PortletURL rowURL = renderResponse.createRenderURL();

	if (!curWikiPage.isNew() && !navigation.equals("draft-pages") && !navigation.equals("pending-pages")) {
		if (portletName.equals(WikiPortletKeys.WIKI_DISPLAY)) {
			rowURL.setParameter("mvcRenderCommandName", "/wiki/view_page");
		}
		else {
			rowURL.setParameter("mvcRenderCommandName", "/wiki/view");
		}

		rowURL.setParameter("redirect", currentURL);

		WikiNode wikiNode = curWikiPage.getNode();

		rowURL.setParameter("nodeName", wikiNode.getName());
	}
	else {
		rowURL.setParameter("mvcRenderCommandName", "/wiki/edit_page");
		rowURL.setParameter("redirect", currentURL);
		rowURL.setParameter("nodeId", String.valueOf(curWikiPage.getNodeId()));
	}

	rowURL.setParameter("title", curWikiPage.getTitle());

	if (navigation.equals("history")) {
		rowURL.setParameter("version", String.valueOf(curWikiPage.getVersion()));
	}

	// Title

	row.addText(HtmlUtil.escape(curWikiPage.getTitle()), rowURL);

	// Status

	row.addStatus(curWikiPage.getStatus(), curWikiPage.getStatusByUserId(), curWikiPage.getStatusDate(), rowURL);

	// Revision

	if (!curWikiPage.isNew()) {
		String revision = String.valueOf(curWikiPage.getVersion());

		if (curWikiPage.isMinorEdit()) {
			revision += " (" + LanguageUtil.get(request, "minor-edit") + ")";
		}

		row.addText(revision, rowURL);
	}
	else {
		row.addText(StringPool.BLANK);
	}

	// User

	if (!curWikiPage.isNew()) {
		row.addText(HtmlUtil.escape(curWikiPage.getUserName()), rowURL);
	}
	else {
		row.addText(StringPool.BLANK);
	}

	// Date

	if (!curWikiPage.isNew()) {
		row.addDate(curWikiPage.getStatusDate(), rowURL);
	}
	else {
		row.addText(StringPool.BLANK);
	}

	// Summary

	if (navigation.equals("history") || navigation.equals("recent-changes")) {
		if (Validator.isNotNull(curWikiPage.getSummary())) {
			row.addText(HtmlUtil.escape(curWikiPage.getSummary()));
		}
		else {
			row.addText(StringPool.BLANK);
		}
	}

	// Action

	if (navigation.equals("history")) {
		if (curWikiPage.isHead()) {
			row.addText(StringPool.BLANK);
		}
		else {
			row.addJSP("/wiki/page_history_action.jsp", "entry-action", application, request, response);
		}
	}

	if (navigation.equals("all-pages") || navigation.equals("categorized-pages") || navigation.equals("draft-pages") || navigation.equals("orphan-pages") || navigation.equals("recent-changes") || navigation.equals("tagged-pages")) {
		row.addJSP("/wiki/page_action.jsp", "entry-action", application, request, response);
	}

	// Add result row

	resultRows.add(row);
}
%>

<c:if test='<%= navigation.equals("history") && (pages.size() > 1) %>'>
	<aui:button-row>
		<aui:button name="compare" primary="<%= true %>" value="compare-versions" />
	</aui:button-row>
</c:if>

<liferay-asset:categorization-filter
	assetType="pages"
	portletURL="<%= portletURL %>"
/>

<liferay-ui:search-iterator
	markupView="deprecated"
	paginate='<%= navigation.equals("history") ? false : true %>'
	searchContainer="<%= searchContainer %>"
/>

<c:if test='<%= navigation.equals("history") %>'>
	<aui:script sandbox="<%= true %>">
		function <portlet:namespace />initRowsChecked() {
			var rowIdsNodes = document.querySelectorAll(
				'input[name=<portlet:namespace />rowIds]'
			);

			Array.prototype.forEach.call(rowIdsNodes, (rowIdsNode, index) => {
				if (index > 1) {
					rowIdsNode.checked = false;
				}
			});
		}

		function <portlet:namespace />updateRowsChecked(element) {
			var rowsChecked = document.querySelectorAll(
				'input[name=<portlet:namespace />rowIds]:checked'
			);

			if (rowsChecked.length > 2) {
				var index = 2;

				if (rowsChecked[2] === element) {
					index = 1;
				}

				rowsChecked[index].checked = false;
			}
		}

		<c:if test="<%= pages.size() > 1 %>">

			<%
			WikiPage latestWikiPage = pages.get(1);
			%>

			var compareButton = document.getElementById('<portlet:namespace />compare');

			compareButton.addEventListener('click', (event) => {
				<portlet:renderURL var="compareVersionURL">
					<portlet:param name="mvcRenderCommandName" value="/wiki/compare_versions" />
					<portlet:param name="backURL" value="<%= currentURL %>" />
					<portlet:param name="tabs3" value="versions" />
					<portlet:param name="nodeId" value="<%= String.valueOf(node.getNodeId()) %>" />
					<portlet:param name="title" value="<%= wikiPage.getTitle() %>" />
					<portlet:param name="type" value="html" />
				</portlet:renderURL>

				var uri = '<%= compareVersionURL %>';

				var rowIds = document.querySelectorAll(
					'input[name=<portlet:namespace />rowIds]:checked'
				);

				if (rowIds.length > 0) {
					var rowIdsSize = rowIds.length;

					if (rowIdsSize === 0 || rowIdsSize === 2) {
						if (rowIdsSize === 0) {
							uri = Liferay.Util.addParams(
								'<portlet:namespace />sourceVersion=<%= latestWikiPage.getVersion() %>',
								uri
							);
							uri = Liferay.Util.addParams(
								'<portlet:namespace />targetVersion=<%= wikiPage.getVersion() %>',
								uri
							);
						}
						else if (rowIdsSize === 2) {
							uri = Liferay.Util.addParams(
								'<portlet:namespace />sourceVersion=' + rowIds[1].value,
								uri
							);
							uri = Liferay.Util.addParams(
								'<portlet:namespace />targetVersion=' + rowIds[0].value,
								uri
							);
						}

						location.href = uri;
					}
				}
			});
		</c:if>

		<portlet:namespace />initRowsChecked();

		var searchContainer = document.getElementById(
			'<portlet:namespace />ocerSearchContainer'
		);

		if (searchContainer) {
			Liferay.Util.delegate(
				searchContainer,
				'click',
				'input[name=<portlet:namespace />rowIds]',
				(event) => {
					<portlet:namespace />updateRowsChecked(event.delegateTarget);
				}
			);
		}
	</aui:script>
</c:if>