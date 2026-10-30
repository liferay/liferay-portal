<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
Folder parentFolder = (Folder)request.getAttribute("info_panel_location.jsp-parentFolder");

long parentFolderId = (parentFolder == null) ? DLFolderConstants.DEFAULT_PARENT_FOLDER_ID : parentFolder.getFolderId();
%>

<c:if test="<%= DLFolderPermission.contains(permissionChecker, scopeGroupId, parentFolderId, ActionKeys.VIEW) %>">
	<dt class="sidebar-dt">
		<liferay-ui:message key="location" />
	</dt>
	<dd class="sidebar-dd">

		<%
		long groupId = GetterUtil.getLong(request.getAttribute("info_panel_location.jsp-groupId"));

		PortletURL viewFolderURL = null;

		if (groupId > 0) {
			viewFolderURL = PortalUtil.getControlPanelPortletURL(request, GroupLocalServiceUtil.getGroup(groupId), PortletProviderUtil.getPortletId(Folder.class.getName(), PortletProvider.Action.MANAGE), groupId, 0, PortletRequest.RENDER_PHASE);
		}
		else {
			viewFolderURL = liferayPortletResponse.createRenderURL();
		}

		if (parentFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			viewFolderURL.setParameter("mvcRenderCommandName", "/document_library/view");
		}
		else {
			viewFolderURL.setParameter("mvcRenderCommandName", "/document_library/view_folder");
			viewFolderURL.setParameter("folderId", String.valueOf(parentFolderId));
		}
		%>

		<clay:link
			displayType="secondary"
			href="<%= viewFolderURL.toString() %>"
			icon="folder"
			label='<%= (parentFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) ? "home" : parentFolder.getName() %>'
		/>
	</dd>
</c:if>