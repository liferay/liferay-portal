<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/wiki/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

WikiNode node = null;

if (row != null) {
	node = (WikiNode)row.getObject();
}
else {
	node = (WikiNode)request.getAttribute("node_info_panel.jsp-wikiNode");
}
%>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message="actions"
	showWhenSingleIcon="<%= true %>"
>
	<c:if test="<%= WikiNodePermission.contains(permissionChecker, node, ActionKeys.UPDATE) %>">
		<portlet:renderURL var="editURL">
			<portlet:param name="mvcRenderCommandName" value="/wiki/edit_node" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="nodeId" value="<%= String.valueOf(node.getNodeId()) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			message="edit"
			url="<%= editURL %>"
		/>
	</c:if>

	<c:if test="<%= WikiNodePermission.contains(permissionChecker, node, ActionKeys.PERMISSIONS) %>">
		<liferay-security:permissionsURL
			modelResource="<%= WikiNode.class.getName() %>"
			modelResourceDescription="<%= node.getName() %>"
			resourcePrimKey="<%= String.valueOf(node.getNodeId()) %>"
			var="permissionsURL"
			windowState="<%= LiferayWindowState.POP_UP.toString() %>"
		/>

		<liferay-ui:icon
			message="permissions"
			method="get"
			url="<%= permissionsURL %>"
			useDialog="<%= true %>"
		/>
	</c:if>

	<c:if test="<%= WikiNodePermission.contains(permissionChecker, node, ActionKeys.IMPORT) %>">
		<portlet:renderURL var="importURL">
			<portlet:param name="mvcRenderCommandName" value="/wiki/import_pages" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="nodeId" value="<%= String.valueOf(node.getNodeId()) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			message="import-pages"
			url="<%= importURL %>"
		/>
	</c:if>

	<c:if test="<%= WikiUtil.isRSSFeedsEnabled(siteWikiGroupServiceConfiguration) %>">
		<liferay-rss:rss
			delta="<%= GetterUtil.getInteger(siteWikiGroupServiceConfiguration.rssDelta()) %>"
			displayStyle="<%= siteWikiGroupServiceConfiguration.rssDisplayStyle() %>"
			feedType="<%= siteWikiGroupServiceConfiguration.rssFeedType() %>"
			url='<%= themeDisplay.getPathMain() + "/wiki/rss?nodeId=" + node.getNodeId() %>'
		/>
	</c:if>

	<c:if test="<%= row != null %>">
		<liferay-util:include page="/wiki/subscribe.jsp" servletContext="<%= application %>" />
	</c:if>

	<c:if test="<%= WikiNodePermission.contains(permissionChecker, node, ActionKeys.UPDATE) %>">
		<portlet:renderURL var="viewDeletedAttachmentsURL">
			<portlet:param name="mvcRenderCommandName" value="/wiki/view_node_deleted_attachments" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="nodeId" value="<%= String.valueOf(node.getNodeId()) %>" />
			<portlet:param name="viewTrashAttachments" value="<%= Boolean.TRUE.toString() %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			message="view-removed-attachments"
			url="<%= viewDeletedAttachmentsURL %>"
		/>
	</c:if>

	<c:if test="<%= WikiNodePermission.contains(permissionChecker, node, ActionKeys.DELETE) && (WikiNodeServiceUtil.getNodesCount(scopeGroupId) > 1) %>">
		<portlet:actionURL name="/wiki/edit_node" var="deleteURL">
			<portlet:param name="<%= Constants.CMD %>" value="<%= trashHelper.isTrashEnabled(scopeGroupId) ? Constants.MOVE_TO_TRASH : Constants.DELETE %>" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="nodeId" value="<%= String.valueOf(node.getNodeId()) %>" />
		</portlet:actionURL>

		<liferay-ui:icon-delete
			trash="<%= trashHelper.isTrashEnabled(scopeGroupId) %>"
			url="<%= deleteURL %>"
		/>
	</c:if>
</liferay-ui:icon-menu>