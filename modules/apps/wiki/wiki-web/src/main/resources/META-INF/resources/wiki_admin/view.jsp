<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/wiki/init.jsp" %>

<%
WikiURLHelper wikiURLHelper = new WikiURLHelper(wikiRequestHelper, renderResponse, wikiGroupServiceConfiguration);

WikiNodesManagementToolbarDisplayContext wikiNodesManagementToolbarDisplayContext = new WikiNodesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, renderRequest, trashHelper);

request.setAttribute("view.jsp-orderByCol", wikiNodesManagementToolbarDisplayContext.getOrderByCol());
request.setAttribute("view.jsp-orderByType", wikiNodesManagementToolbarDisplayContext.getOrderByType());
%>

<portlet:actionURL name="/wiki/edit_node" var="restoreTrashEntriesURL">
	<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.RESTORE %>" />
</portlet:actionURL>

<clay:management-toolbar
	actionDropdownItems="<%= wikiNodesManagementToolbarDisplayContext.getActionDropdownItems() %>"
	additionalProps="<%= wikiNodesManagementToolbarDisplayContext.getAdditionalProps() %>"
	creationMenu="<%= wikiNodesManagementToolbarDisplayContext.getCreationMenu() %>"
	disabled="<%= wikiNodesManagementToolbarDisplayContext.isDisabled() %>"
	infoPanelId="infoPanelId"
	itemsTotal="<%= wikiNodesManagementToolbarDisplayContext.getTotalItems() %>"
	orderDropdownItems="<%= wikiNodesManagementToolbarDisplayContext.getOrderByDropdownItems() %>"
	propsTransformer="{WikiNodesManagementToolbarPropsTransformer} from wiki-web"
	searchContainerId="wikiNodes"
	selectable="<%= wikiNodesManagementToolbarDisplayContext.isSelectable() %>"
	showInfoButton="<%= true %>"
	showSearch="<%= wikiNodesManagementToolbarDisplayContext.isShowSearch() %>"
	sortingOrder="<%= wikiNodesManagementToolbarDisplayContext.getSortingOrder() %>"
	sortingURL="<%= String.valueOf(wikiNodesManagementToolbarDisplayContext.getSortingURL()) %>"
	viewTypeItems="<%= wikiNodesManagementToolbarDisplayContext.getViewTypes() %>"
/>

<div class="closed sidenav-container sidenav-right" id="<portlet:namespace />infoPanelId">
	<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/wiki/node_info_panel" var="sidebarPanelURL" />

	<liferay-frontend:sidebar-panel
		resourceURL="<%= sidebarPanelURL %>"
		searchContainerId="wikiNodes"
	>

		<%
		request.removeAttribute(WikiWebKeys.WIKI_NODE);
		%>

		<liferay-util:include page="/wiki_admin/node_info_panel.jsp" servletContext="<%= application %>" />
	</liferay-frontend:sidebar-panel>

	<clay:container-fluid
		cssClass="container-view sidenav-content"
	>

		<%
		PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "wiki"), String.valueOf(wikiNodesManagementToolbarDisplayContext.getPortletURL()));
		%>

		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, false, true) %>"
		/>

		<liferay-trash:undo
			portletURL="<%= restoreTrashEntriesURL %>"
		/>

		<liferay-ui:error exception="<%= RequiredNodeException.class %>" message="the-last-main-node-is-required-and-cannot-be-deleted" />

		<aui:form action="<%= wikiURLHelper.getSearchURL() %>" method="get" name="fm">
			<aui:input name="<%= Constants.CMD %>" type="hidden" />
			<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

			<liferay-ui:search-container
				id="wikiNodes"
				searchContainer="<%= wikiNodesManagementToolbarDisplayContext.getSearchContainer() %>"
				total="<%= wikiNodesManagementToolbarDisplayContext.getSearchContainerTotal() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.wiki.model.WikiNode"
					keyPropertyName="nodeId"
					modelVar="node"
				>

					<%
					row.setData(
						HashMapBuilder.<String, Object>put(
							"actions", StringUtil.merge(wikiNodesManagementToolbarDisplayContext.getAvailableActions(node))
						).build());

					PortletURL rowURL = PortletURLBuilder.createRenderURL(
						renderResponse
					).setMVCRenderCommandName(
						"/wiki/view_pages"
					).setRedirect(
						currentURL
					).setNavigation(
						"all-pages"
					).setParameter(
						"nodeId", node.getNodeId()
					).buildPortletURL();
					%>

					<c:choose>
						<c:when test='<%= Objects.equals(wikiNodesManagementToolbarDisplayContext.getDisplayStyle(), "descriptive") %>'>
							<liferay-ui:search-container-column-icon
								icon="wiki"
								toggleRowChecker="<%= true %>"
							/>

							<liferay-ui:search-container-column-text
								colspan="<%= 2 %>"
							>
								<p class="h5">
									<aui:a href="<%= rowURL.toString() %>">
										<%= HtmlUtil.escape(node.getName()) %>
									</aui:a>
								</p>

								<%
								Date lastPostDate = node.getLastPostDate();
								%>

								<c:if test="<%= lastPostDate != null %>">
									<span class="text-default">
										<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - lastPostDate.getTime(), true) %>" key="last-post-x-ago" />
									</span>
								</c:if>

								<span class="text-default">
									<liferay-ui:message arguments="<%= String.valueOf(WikiPageServiceUtil.getPagesCount(scopeGroupId, node.getNodeId(), true)) %>" key="x-pages" />
								</span>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-jsp
								path="/wiki/node_action.jsp"
							/>
						</c:when>
						<c:otherwise>
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand table-cell-minw-200 table-title"
								href="<%= rowURL %>"
								name="wiki"
								value="<%= HtmlUtil.escape(node.getName()) %>"
							/>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-small"
								name="num-of-pages"
								value="<%= String.valueOf(WikiPageServiceUtil.getPagesCount(scopeGroupId, node.getNodeId(), true)) %>"
							/>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smaller table-cell-ws-nowrap"
								name="last-post-date"
								value='<%= (node.getLastPostDate() == null) ? LanguageUtil.get(request, "never") : dateTimeFormat.format(node.getLastPostDate()) %>'
							/>

							<liferay-ui:search-container-column-jsp
								path="/wiki/node_action.jsp"
							/>
						</c:otherwise>
					</c:choose>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					displayStyle="<%= wikiNodesManagementToolbarDisplayContext.getDisplayStyle() %>"
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</aui:form>
	</clay:container-fluid>
</div>