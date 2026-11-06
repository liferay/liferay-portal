<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DLSelectFolderDisplayContext dlSelectFolderDisplayContext = (DLSelectFolderDisplayContext)request.getAttribute(DLSelectFolderDisplayContext.class.getName());

DLBreadcrumbUtil.addPortletBreadcrumbEntries(ParamUtil.getString(request, "displayStyle"), dlSelectFolderDisplayContext.getFolder(), request, liferayPortletResponse, dlSelectFolderDisplayContext.getIteratorPortletURL(liferayPortletResponse), dlSelectFolderDisplayContext.getRepositoryId(), dlSelectFolderDisplayContext.isShowGroupSelector());
%>

<clay:container-fluid>
	<aui:form method="post" name="selectFolderFm">
		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, false, true) %>"
		/>

		<aui:button-row>
			<c:if test="<%= dlSelectFolderDisplayContext.hasAddFolderPermission() %>">
				<aui:button href="<%= String.valueOf(dlSelectFolderDisplayContext.getAddFolderPortletURL()) %>" value="add-folder" />
			</c:if>

			<aui:button cssClass="selector-button" data="<%= dlSelectFolderDisplayContext.getSelectorButtonData() %>" disabled="<%= dlSelectFolderDisplayContext.isSelectButtonDisabled() %>" value="select-this-folder" />
		</aui:button-row>

		<liferay-ui:search-container
			cssClass="pb-6"
			iteratorURL="<%= dlSelectFolderDisplayContext.getIteratorPortletURL(liferayPortletResponse) %>"
			total="<%= dlSelectFolderDisplayContext.getFoldersCount() %>"
		>
			<liferay-ui:search-container-results
				results="<%= dlSelectFolderDisplayContext.getFolders(searchContainer.getStart(), searchContainer.getEnd()) %>"
			/>

			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.repository.model.Folder"
				keyPropertyName="folderId"
				modelVar="curFolder"
				rowVar="row"
			>

				<%
				int folderFileEntriesCount = dlSelectFolderDisplayContext.getFolderFileEntriesCount(curFolder);
				int folderFoldersCount = dlSelectFolderDisplayContext.getFolderFoldersCount(curFolder);
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand table-cell-minw-200"
					name="folder"
				>
					<clay:link
						href="<%= String.valueOf(dlSelectFolderDisplayContext.getRowPortletURL(curFolder, liferayPortletResponse)) %>"
						icon="<%= dlSelectFolderDisplayContext.getIconCssClass(curFolder) %>"
						label="<%= HtmlUtil.escape(curFolder.getName()) %>"
						title="<%= HtmlUtil.escape(curFolder.getName()) %>"
					/>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-column-text-end"
					href="<%= dlSelectFolderDisplayContext.getRowPortletURL(curFolder, liferayPortletResponse) %>"
					name="folders"
					value="<%= String.valueOf(folderFoldersCount) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-column-text-end"
					href="<%= dlSelectFolderDisplayContext.getRowPortletURL(curFolder, liferayPortletResponse) %>"
					name="documents"
					value="<%= String.valueOf(folderFileEntriesCount) %>"
				/>

				<liferay-ui:search-container-column-text>
					<aui:button cssClass="selector-button" data="<%= dlSelectFolderDisplayContext.getSelectorButtonData(curFolder) %>" disabled="<%= dlSelectFolderDisplayContext.isSelectButtonDisabled(curFolder.getFolderId(), curFolder.getRepositoryId()) %>" value="select" />
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>