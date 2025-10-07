<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PortletURL portletURL = PortletURLBuilder.create(
	currentURLObj
).setParameter(
	"historyKey", liferayPortletResponse.getNamespace() + "images"
).buildPortletURL();

CategoryCPAttachmentFileEntriesManagementToolbarDisplayContext categoryCPAttachmentFileEntriesManagementToolbarDisplayContext = new CategoryCPAttachmentFileEntriesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, portletURL);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= categoryCPAttachmentFileEntriesManagementToolbarDisplayContext %>"
/>

<clay:container-fluid>
	<liferay-ui:search-container
		emptyResultsMessage="there-are-no-images"
		id="cpAttachmentFileEntries"
		searchContainer="<%= categoryCPAttachmentFileEntriesManagementToolbarDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.commerce.product.model.CPAttachmentFileEntry"
			keyProperty="CPAttachmentFileEntryId"
			modelVar="cpAttachmentFileEntry"
		>

			<%
			String thumbnailSrc = StringPool.BLANK;

			FileEntry fileEntry = cpAttachmentFileEntry.fetchFileEntry();

			if (fileEntry == null) {
				thumbnailSrc = cpAttachmentFileEntry.getCDNURL();
			}
			else {
				thumbnailSrc = CommerceMediaResolverUtil.getThumbnailURL(AccountConstants.ACCOUNT_ENTRY_ID_GUEST, cpAttachmentFileEntry.getCPAttachmentFileEntryId());
			}
			%>

			<c:choose>
				<c:when test="<%= Validator.isNotNull(thumbnailSrc) %>">
					<liferay-ui:search-container-column-image
						name="image"
						src="<%= thumbnailSrc %>"
					/>
				</c:when>
				<c:otherwise>
					<liferay-ui:search-container-column-icon
						icon="documents-and-media"
					/>
				</c:otherwise>
			</c:choose>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="title"
				value="<%= HtmlUtil.escape(cpAttachmentFileEntry.getTitle(languageId)) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
				name="extension[file]"
				value="<%= HtmlUtil.escape(fileEntry.getExtension()) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
				property="priority"
			/>

			<liferay-ui:search-container-column-status
				cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
				name="status"
				status="<%= cpAttachmentFileEntry.getStatus() %>"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
				name="modified-date"
				property="modifiedDate"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
				name="display-date"
				property="displayDate"
			/>

			<liferay-ui:search-container-column-jsp
				path="/image_action.jsp"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>