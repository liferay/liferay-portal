<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/item/selector/init.jsp" %>

<%
WikiPageItemSelectorViewDisplayContext wikiPageItemSelectorViewDisplayContext = (WikiPageItemSelectorViewDisplayContext)request.getAttribute(WikiItemSelectorWebKeys.WIKI_PAGE_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT);

SearchContainer<WikiPage> wikiPagesSearchContainer = wikiPageItemSelectorViewDisplayContext.getSearchContainer(request, liferayPortletResponse, renderRequest);
%>

<aui:style type="text/css">
	.portlet-item-selector .wiki-page-item {
		cursor: pointer;
	}
</aui:style>

<%
String searchURL = HttpComponentsUtil.removeParameter(
	PortletURLBuilder.create(
		PortletURLUtil.clone(currentURLObj, liferayPortletResponse)
	).setParameter(
		"resetCur", true
	).buildString(),
	liferayPortletResponse.getNamespace() + "keywords");
%>

<clay:management-toolbar
	clearResultsURL="<%= searchURL %>"
	itemsTotal="<%= wikiPagesSearchContainer.getTotal() %>"
	searchActionURL="<%= searchURL %>"
	selectable="<%= false %>"
	showCreationMenu="<%= false %>"
/>

<clay:container-fluid
	cssClass="lfr-item-viewer"
	id='<%= liferayPortletResponse.getNamespace() + "wikiPagesSelectorContainer" %>'
>
	<liferay-ui:search-container
		id="wikiPagesSearchContainer"
		searchContainer="<%= wikiPagesSearchContainer %>"
		total="<%= wikiPagesSearchContainer.getTotal() %>"
	>
		<liferay-ui:search-container-results
			results="<%= wikiPagesSearchContainer.getResults() %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.wiki.model.WikiPage"
			cssClass="wiki-page-item"
			keyProperty="pageId"
			modelVar="curPage"
		>
			<liferay-ui:search-container-column-icon
				icon="wiki-page"
			/>

			<liferay-ui:search-container-column-text
				colspan="<%= 2 %>"
			>

				<%
				Date modifiedDate = curPage.getModifiedDate();

				String modifiedDateDescription = LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - modifiedDate.getTime(), true);
				%>

				<div class="h5 text-default">
					<c:choose>
						<c:when test="<%= Validator.isNotNull(curPage.getUserName()) %>">
							<liferay-ui:message arguments="<%= new String[] {HtmlUtil.escape(curPage.getUserName()), modifiedDateDescription} %>" key="x-modified-x-ago" />
						</c:when>
						<c:otherwise>
							<liferay-ui:message arguments="<%= modifiedDateDescription %>" key="modified-x-ago" />
						</c:otherwise>
					</c:choose>
				</div>

				<%
				WikiPageItemSelectorReturnTypeResolver wikiPageItemSelectorReturnTypeResolver = wikiPageItemSelectorViewDisplayContext.getWikiPageItemSelectorReturnTypeResolver();
				%>

				<div class="h4">
					<a class="wiki-page" data-title="<%= HtmlUtil.escape(wikiPageItemSelectorReturnTypeResolver.getTitle(curPage, themeDisplay)) %>" data-value="<%= HtmlUtil.escape(wikiPageItemSelectorReturnTypeResolver.getValue(curPage, themeDisplay)) %>" href="javascript:void(0);">
						<%= curPage.getTitle() %>
					</a>
				</div>

				<div class="h5 text-default">
					<aui:workflow-status markupView="lexicon" showIcon="<%= false %>" showLabel="<%= false %>" status="<%= curPage.getStatus() %>" />
				</div>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="descriptive"
			markupView="lexicon"
			searchContainer="<%= wikiPagesSearchContainer %>"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>

<aui:script use="liferay-search-container">
	var Util = Liferay.Util;

	var searchContainer = Liferay.SearchContainer.get(
		'<portlet:namespace />wikiPagesSearchContainer'
	);

	var searchContainerContentBox = searchContainer.get('contentBox');

	searchContainerContentBox.delegate(
		'click',
		(event) => {
			var selectedItem = event.currentTarget;

			var linkItem = selectedItem.one('.wiki-page');

			Util.getOpener().Liferay.fire(
				'<%= wikiPageItemSelectorViewDisplayContext.getItemSelectedEventName() %>',
				{
					data: {
						title: linkItem.attr('data-title'),
						value: linkItem.attr('data-value'),
					},
				}
			);

			selectedItem.siblings().removeClass('active');
			selectedItem.addClass('active');
		},
		'.list-group-item'
	);
</aui:script>