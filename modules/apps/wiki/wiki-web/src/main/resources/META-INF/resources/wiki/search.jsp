<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/wiki/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

WikiNode node = (WikiNode)request.getAttribute(WikiWebKeys.WIKI_NODE);

long nodeId = BeanParamUtil.getLong(node, request, "nodeId");

long[] nodeIds = null;

if (node != null) {
	nodeIds = new long[] {nodeId};
}

String keywords = ParamUtil.getString(request, "keywords");

boolean createNewPage = WikiNodePermission.contains(permissionChecker, node, ActionKeys.ADD_PAGE);

WikiURLHelper wikiURLHelper = new WikiURLHelper(wikiRequestHelper, renderResponse, wikiGroupServiceConfiguration);
%>

<aui:form action="<%= wikiURLHelper.getSearchURL() %>" method="get" name="fm">
	<liferay-portlet:renderURLParams portletURL="<%= wikiURLHelper.getSearchURL() %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="nodeId" type="hidden" value="<%= nodeId %>" />

	<liferay-ui:header
		backURL="<%= redirect %>"
		title="search"
	/>

	<div class="form-search">
		<div class="input-group">
			<div class="input-group-item">
				<input aria-label="<%= LanguageUtil.get(request, "search-pages") %>" class="form-control input-group-inset input-group-inset-after search-query" data-qa-id="searchInput" id="<portlet:namespace />keywords" name="<portlet:namespace />keywords" placeholder="<%= LanguageUtil.get(request, "keywords") %>" title="<%= LanguageUtil.get(request, "search-pages") %>" type="text" value="<%= HtmlUtil.escapeAttribute(ParamUtil.getString(request, "keywords")) %>" />

				<div class="input-group-inset-item input-group-inset-item-after">
					<clay:button
						data-qa-id="searchButton"
						displayType="unstyled"
						icon="search"
						monospaced="<%= false %>"
						type="submit"
					/>
				</div>
			</div>
		</div>
	</div>

	<%
	WikiSearchDisplayContext wikiSearchDisplayContext = new WikiSearchDisplayContext(request, renderRequest, renderResponse, wikiPortletInstanceSettingsHelper);
	%>

	<liferay-ui:search-container
		searchContainer="<%= wikiSearchDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.search.SearchResult"
			modelVar="searchResult"
		>

			<%
			WikiPage wikiPage = WikiPageLocalServiceUtil.getPage(searchResult.getClassPK());

			String title = wikiPage.getTitle();

			if (StringUtil.equalsIgnoreCase(title, keywords)) {
				createNewPage = false;
			}

			WikiNode curNode = wikiPage.getNode();

			PortletURL viewPageURL = renderResponse.createRenderURL();

			if (portletName.equals(WikiPortletKeys.WIKI_DISPLAY)) {
				viewPageURL.setParameter("mvcRenderCommandName", "/wiki/view_page");
			}
			else {
				viewPageURL.setParameter("mvcRenderCommandName", "/wiki/view");
			}

			viewPageURL.setParameter("nodeName", node.getName());
			viewPageURL.setParameter("title", wikiPage.getTitle());

			PortletURL editPageURL = PortletURLBuilder.createRenderURL(
				renderResponse
			).setMVCRenderCommandName(
				"/wiki/edit_page"
			).setRedirect(
				currentURL
			).setParameter(
				"nodeId", node.getNodeId()
			).setParameter(
				"title", title
			).buildPortletURL();

			WikiEngineRenderer wikiEngineRenderer = (WikiEngineRenderer)request.getAttribute(WikiWebKeys.WIKI_ENGINE_RENDERER);

			String formattedContent = null;

			try {
				formattedContent = WikiUtil.getFormattedContent(wikiEngineRenderer, renderRequest, renderResponse, wikiPage, viewPageURL, editPageURL, wikiPage.getTitle(), false);
			}
			catch (Exception e) {
				formattedContent = wikiPage.getContent();
			}
			%>

			<portlet:renderURL var="rowURL">
				<portlet:param name="mvcRenderCommandName" value="/wiki/view" />
				<portlet:param name="nodeName" value="<%= curNode.getName() %>" />
				<portlet:param name="title" value="<%= title %>" />
			</portlet:renderURL>

			<%@ include file="/wiki/app_view_search_entry.jspf" %>
		</liferay-ui:search-container-row>

		<liferay-ui:search-paginator
			searchContainer="<%= searchContainer %>"
			type="more"
		/>
	</liferay-ui:search-container>

	<c:if test="<%= createNewPage %>">
		<portlet:renderURL var="addPageURL">
			<portlet:param name="mvcRenderCommandName" value="/wiki/edit_page" />
			<portlet:param name="redirect" value="<%= redirect %>" />
			<portlet:param name="nodeId" value="<%= String.valueOf(nodeId) %>" />
			<portlet:param name="title" value="<%= keywords %>" />
			<portlet:param name="editTitle" value="1" />
		</portlet:renderURL>

		<strong><aui:a cssClass="new-page" href="<%= addPageURL.toString() %>" label="create-a-new-page-on-this-topic" /></strong>
	</c:if>
</aui:form>