<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/tree_menu/init.jsp" %>

<%
String title = ParamUtil.getString(request, "title", wikiGroupServiceConfiguration.frontPageName());

PortletURL viewURL = PortletURLBuilder.createRenderURL(
	liferayPortletResponse, PortletProviderUtil.getPortletId(WikiPage.class.getName(), PortletProvider.Action.VIEW)
).setMVCRenderCommandName(
	"/wiki/view_page"
).buildPortletURL();

List<MenuItem> menuItems = MenuItem.fromWikiNode(selNodeId, depth, viewURL);
%>

<c:choose>
	<c:when test="<%= !menuItems.isEmpty() %>">
		<div class="loading tree-view-container">
			<span aria-hidden="true" class="loading-animation loading-animation-sm"></span>
			<%= _buildTreeMenuHTML(menuItems, title, true) %>

			<aui:script use="aui-tree-view">
				var wikiPageList = A.one(
					'.wiki-navigation-portlet-tree-menu .loading .tree-menu'
				);
				var wikiPageListContainer = wikiPageList.ancestor('.tree-view-container');

				var treeView = new A.TreeView({
					contentBox: wikiPageList,
				}).render();

				wikiPageListContainer.removeClass('loading');

				var selected = wikiPageList.one('.tree-node .tag-selected');

				if (selected) {
					var selectedChild = treeView.getNodeByChild(selected);

					selectedChild.expand();

					selectedChild.eachParent((node) => {
						if (node instanceof A.TreeNode) {
							node.expand();
						}
					});
				}
			</aui:script>
		</div>
	</c:when>
	<c:otherwise>
		<liferay-ui:message key="no-wiki-pages-were-found" />
	</c:otherwise>
</c:choose>

<%!
private String _buildTreeMenuHTML(List<MenuItem> menuItems, String curTitle, boolean isRoot) {
	StringBuilder sb = new StringBuilder();

	if (isRoot) {
		sb.append("<ul class=\"tree-menu\">");
	}

	for (MenuItem menuItem : menuItems) {
		String name = menuItem.getName();
		String url = menuItem.getURL();

		sb.append("<li class=\"tree-node\">");

		if (Validator.isNotNull(url)) {
			sb.append("<a ");

			if (name.equals(curTitle)) {
				sb.append("class=\"tag-selected\" ");
			}

			sb.append("href=\"");
			sb.append(HtmlUtil.escapeAttribute(url));
			sb.append("\">");
			sb.append(HtmlUtil.escape(name));
			sb.append("</a>");
		}
		else {
			sb.append(HtmlUtil.escape(name));
		}

		if (!menuItem.getChildren().isEmpty()) {
			sb.append("<ul>");
			sb.append(_buildTreeMenuHTML(menuItem.getChildren(), curTitle, false));
			sb.append("</ul>");
		}

		sb.append("</li>");
	}

	if (isRoot) {
		sb.append("</ul>");
	}

	return sb.toString();
}
%>