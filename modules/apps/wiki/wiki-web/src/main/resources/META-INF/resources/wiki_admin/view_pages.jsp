<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/wiki/init.jsp" %>

<%
WikiNode node = (WikiNode)request.getAttribute(WikiWebKeys.WIKI_NODE);

String navigation = ParamUtil.getString(request, "navigation", "all-pages");

String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = PortletURLUtil.clone(currentURLObj, liferayPortletResponse);

WikiListPagesDisplayContext wikiListPagesDisplayContext = new WikiListPagesDisplayContext(request, (TrashHelper)request.getAttribute(TrashWebKeys.TRASH_HELPER), node);

SearchContainer<WikiPage> wikiPagesSearchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, currentURLObj, null, wikiListPagesDisplayContext.getEmptyResultsMessage());

if (Validator.isNull(keywords)) {
	String orderByCol = ParamUtil.getString(request, "orderByCol");
	String orderByType = ParamUtil.getString(request, "orderByType");

	if (Validator.isNotNull(orderByCol) && Validator.isNotNull(orderByType)) {
		portalPreferences.setValue(WikiPortletKeys.WIKI_ADMIN, "pages-order-by-col", orderByCol);
		portalPreferences.setValue(WikiPortletKeys.WIKI_ADMIN, "pages-order-by-type", orderByType);
	}
	else {
		orderByCol = portalPreferences.getValue(WikiPortletKeys.WIKI_ADMIN, "pages-order-by-col", "modifiedDate");
		orderByType = portalPreferences.getValue(WikiPortletKeys.WIKI_ADMIN, "pages-order-by-type", "desc");
	}

	request.setAttribute("view_pages.jsp-orderByCol", orderByCol);
	request.setAttribute("view_pages.jsp-orderByType", orderByType);

	wikiPagesSearchContainer.setOrderByCol(orderByCol);
	wikiPagesSearchContainer.setOrderByType(orderByType);
}

wikiPagesSearchContainer.setRowChecker(new PagesChecker(liferayPortletRequest, liferayPortletResponse));

wikiListPagesDisplayContext.populateResultsAndTotal(wikiPagesSearchContainer);

WikiURLHelper wikiURLHelper = new WikiURLHelper(wikiRequestHelper, renderResponse, wikiGroupServiceConfiguration);

PortletURL backToNodeURL = wikiURLHelper.getBackToNodeURL(node);

String displayStyle = ParamUtil.getString(request, "displayStyle");

if (Validator.isNull(displayStyle)) {
	displayStyle = portalPreferences.getValue(WikiPortletKeys.WIKI_ADMIN, "pages-display-style", "descriptive");
}
else {
	portalPreferences.setValue(WikiPortletKeys.WIKI_ADMIN, "pages-display-style", displayStyle);

	request.setAttribute(WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);
}

WikiPagesManagementToolbarDisplayContext wikiPagesManagementToolbarDisplayContext = new WikiPagesManagementToolbarDisplayContext(displayStyle, liferayPortletRequest, liferayPortletResponse, wikiPagesSearchContainer, trashHelper, wikiURLHelper);
%>

<liferay-util:include page="/wiki_admin/pages_navigation.jsp" servletContext="<%= application %>" />

<clay:management-toolbar
	actionDropdownItems="<%= wikiPagesManagementToolbarDisplayContext.getActionDropdownItems() %>"
	additionalProps="<%= wikiPagesManagementToolbarDisplayContext.getAdditionalProps() %>"
	clearResultsURL="<%= String.valueOf(wikiPagesManagementToolbarDisplayContext.getClearResultsURL()) %>"
	creationMenu="<%= wikiPagesManagementToolbarDisplayContext.getCreationMenu() %>"
	disabled="<%= wikiPagesManagementToolbarDisplayContext.isDisabled() %>"
	filterDropdownItems="<%= wikiPagesManagementToolbarDisplayContext.getFilterDropdownItems() %>"
	filterLabelItems="<%= wikiPagesManagementToolbarDisplayContext.getFilterLabelItems() %>"
	infoPanelId="infoPanelId"
	itemsTotal="<%= wikiPagesManagementToolbarDisplayContext.getTotalItems() %>"
	orderDropdownItems="<%= wikiPagesManagementToolbarDisplayContext.getOrderByDropdownItems() %>"
	propsTransformer="{WikiPagesManagementToolbarPropsTransformer} from wiki-web"
	searchActionURL="<%= String.valueOf(wikiPagesManagementToolbarDisplayContext.getSearchActionURL()) %>"
	searchContainerId="wikiPages"
	selectable="<%= wikiPagesManagementToolbarDisplayContext.isSelectable() %>"
	showInfoButton="<%= wikiPagesManagementToolbarDisplayContext.isShowInfoButton() %>"
	showSearch="<%= wikiPagesManagementToolbarDisplayContext.isShowSearch() %>"
	sortingOrder="<%= wikiPagesManagementToolbarDisplayContext.getSortingOrder() %>"
	sortingURL="<%= String.valueOf(wikiPagesManagementToolbarDisplayContext.getSortingURL()) %>"
	viewTypeItems="<%= wikiPagesManagementToolbarDisplayContext.getViewTypes() %>"
/>

<div class="closed sidenav-container sidenav-right" id="<portlet:namespace />infoPanelId">
	<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/wiki/page_info_panel" var="sidebarPanelURL">
		<portlet:param name="nodeId" value="<%= String.valueOf(node.getNodeId()) %>" />
		<portlet:param name="showSidebarHeader" value="<%= Boolean.TRUE.toString() %>" />
	</liferay-portlet:resourceURL>

	<liferay-frontend:sidebar-panel
		resourceURL="<%= sidebarPanelURL %>"
		searchContainerId="wikiPages"
	>

		<%
		request.setAttribute(WikiWebKeys.SHOW_SIDEBAR_HEADER, true);
		request.setAttribute(WikiWebKeys.WIKI_NODE, node);
		%>

		<liferay-util:include page="/wiki_admin/page_info_panel.jsp" servletContext="<%= application %>" />
	</liferay-frontend:sidebar-panel>

	<clay:container-fluid
		cssClass="container-view sidenav-content"
	>

		<%
		PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "wiki"), backToNodeURL.toString());

		PortalUtil.addPortletBreadcrumbEntry(request, node.getName(), portletURL.toString());
		%>

		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, false, true) %>"
		/>

		<%
		WikiVisualizationHelper wikiVisualizationHelper = new WikiVisualizationHelper(wikiRequestHelper, wikiPortletInstanceSettingsHelper, wikiGroupServiceConfiguration);
		%>

		<c:if test="<%= wikiVisualizationHelper.isUndoTrashControlVisible() %>">

			<%
			PortletURL undoTrashURL = wikiURLHelper.getUndoTrashURL();
			%>

			<liferay-trash:undo
				portletURL="<%= undoTrashURL.toString() %>"
			/>
		</c:if>

		<aui:form action="<%= wikiURLHelper.getSearchURL() %>" method="get" name="fm">
			<aui:input name="nodeId" type="hidden" value="<%= String.valueOf(node.getNodeId()) %>" />
			<aui:input name="<%= Constants.CMD %>" type="hidden" />
			<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

			<liferay-ui:search-container
				id="wikiPages"
				searchContainer="<%= wikiPagesSearchContainer %>"
				total="<%= wikiPagesSearchContainer.getTotal() %>"
			>
				<liferay-ui:search-container-results
					results="<%= wikiPagesSearchContainer.getResults() %>"
				/>

				<liferay-ui:search-container-row
					className="com.liferay.wiki.model.WikiPage"
					keyProperty="pageId"
					modelVar="curPage"
				>

					<%
					row.setData(
						HashMapBuilder.<String, Object>put(
							"actions", StringUtil.merge(wikiPagesManagementToolbarDisplayContext.getAvailableActions(curPage))
						).build());

					PortletURL rowURL = renderResponse.createRenderURL();

					if (!navigation.equals("draft-pages") || Validator.isNotNull(keywords)) {
						rowURL.setParameter("mvcRenderCommandName", "/wiki/view");
						rowURL.setParameter("redirect", currentURL);

						WikiNode wikiNode = curPage.getNode();

						rowURL.setParameter("nodeName", wikiNode.getName());
					}
					else {
						rowURL.setParameter("mvcRenderCommandName", "/wiki/edit_page");
						rowURL.setParameter("redirect", currentURL);
						rowURL.setParameter("nodeId", String.valueOf(curPage.getNodeId()));
					}

					rowURL.setParameter("title", curPage.getTitle());
					%>

					<c:choose>
						<c:when test='<%= displayStyle.equals("descriptive") %>'>
							<liferay-ui:search-container-column-icon
								icon="wiki-page"
								toggleRowChecker="<%= true %>"
							/>

							<liferay-ui:search-container-column-text
								colspan="<%= 2 %>"
							>
								<h2 class="h5">
									<aui:a href="<%= rowURL.toString() %>">
										<%= HtmlUtil.escape(curPage.getTitle()) %>
									</aui:a>
								</h2>

								<%
								Date modifiedDate = curPage.getModifiedDate();

								String modifiedDateDescription = LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - modifiedDate.getTime(), true);
								%>

								<span class="text-default">
									<c:choose>
										<c:when test="<%= Validator.isNotNull(curPage.getUserName()) %>">
											<liferay-ui:message arguments="<%= new String[] {HtmlUtil.escape(curPage.getUserName()), modifiedDateDescription} %>" key="x-modified-x-ago" />
										</c:when>
										<c:otherwise>
											<liferay-ui:message arguments="<%= modifiedDateDescription %>" key="modified-x-ago" />
										</c:otherwise>
									</c:choose>
								</span>
								<span class="text-default">
									<aui:workflow-status markupView="lexicon" showIcon="<%= false %>" showLabel="<%= false %>" status="<%= curPage.getStatus() %>" />
								</span>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-jsp
								path="/wiki/page_action.jsp"
							/>
						</c:when>
						<c:otherwise>
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand table-cell-minw-200 table-title"
								href="<%= rowURL %>"
								name="title"
								value="<%= curPage.getTitle() %>"
							/>

							<liferay-ui:search-container-column-status
								cssClass="table-cell-expand-smaller"
								name="status"
								status="<%= curPage.getStatus() %>"
							/>

							<%
							String revision = String.valueOf(curPage.getVersion());

							if (curPage.isMinorEdit()) {
								revision += " (" + LanguageUtil.get(request, "minor-edit") + ")";
							}
							%>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smaller table-cell-minw-150"
								name="revision"
								value="<%= revision %>"
							/>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smaller table-cell-minw-150"
								name="user"
								value="<%= HtmlUtil.escape(curPage.getUserName()) %>"
							/>

							<liferay-ui:search-container-column-date
								cssClass="table-cell-ws-nowrap"
								name="modified-date"
								value="<%= curPage.getModifiedDate() %>"
							/>

							<liferay-ui:search-container-column-jsp
								path="/wiki/page_action.jsp"
							/>
						</c:otherwise>
					</c:choose>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					displayStyle="<%= displayStyle %>"
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</aui:form>
	</clay:container-fluid>
</div>