<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/asset/init.jsp" %>

<%
JournalFolderDisplayContext journalFolderDisplayContext = new JournalFolderDisplayContext(request);

JournalFolder folder = journalFolderDisplayContext.getFolder();
%>

<c:if test="<%= folder != null %>">
	<div class="aspect-ratio aspect-ratio-8-to-3 bg-light mb-4">
		<div class="aspect-ratio-item-center-middle aspect-ratio-item-fluid card-type-asset-icon">
			<div class="text-secondary">
				<svg aria-hidden="true" class="h4 lexicon-icon lexicon-icon-folder reference-mark">
					<use xlink:href="<%= themeDisplay.getPathThemeSpritemap() %>#folder" />
				</svg>
			</div>
		</div>
	</div>

	<c:if test="<%= Validator.isNotNull(folder.getDescription()) %>">
		<div class="asset-content mb-3">
			<%= HtmlUtil.replaceNewLine(HtmlUtil.escape(folder.getDescription())) %>
		</div>
	</c:if>

	<div class="asset-details mb-3">

		<%
		int foldersCount = JournalFolderServiceUtil.getFoldersCount(folder.getGroupId(), folder.getFolderId(), journalFolderDisplayContext.getStatus());
		%>

		<%= foldersCount %> <liferay-ui:message key='<%= (foldersCount == 1) ? "subfolder" : "subfolders" %>' />
	</div>

	<div class="asset-details mb-3">

		<%
		int entriesCount = JournalArticleServiceUtil.getArticlesCount(folder.getGroupId(), folder.getFolderId(), journalFolderDisplayContext.getStatus());
		%>

		<%= entriesCount %> <liferay-ui:message key='<%= (entriesCount == 1) ? "article" : "articles" %>' />
	</div>

	<div class="asset-custom-attributes">
		<liferay-expando:custom-attributes-available
			className="<%= JournalFolder.class.getName() %>"
		>
			<liferay-expando:custom-attribute-list
				className="<%= JournalFolder.class.getName() %>"
				classPK="<%= folder.getFolderId() %>"
				editable="<%= false %>"
				label="<%= true %>"
			/>
		</liferay-expando:custom-attributes-available>
	</div>
</c:if>