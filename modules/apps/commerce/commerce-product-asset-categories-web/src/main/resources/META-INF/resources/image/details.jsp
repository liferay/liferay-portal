<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CategoryCPAttachmentFileEntriesDisplayContext categoryCPAttachmentFileEntriesDisplayContext = (CategoryCPAttachmentFileEntriesDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPAttachmentFileEntry cpAttachmentFileEntry = categoryCPAttachmentFileEntriesDisplayContext.getCPAttachmentFileEntry();

long categoryId = ParamUtil.getLong(request, "categoryId");
%>

<liferay-ui:error-marker
	key="<%= WebKeys.ERROR_SECTION %>"
	value="details"
/>

<aui:model-context bean="<%= cpAttachmentFileEntry %>" model="<%= CPAttachmentFileEntry.class %>" />

<liferay-ui:error exception="<%= NoSuchFileEntryException.class %>" message="please-select-an-existing-file" />

<portlet:actionURL name="/commerce_product_asset_categories/upload_temp_asset_category_attachment" var="uploadCoverImageURL">
	<portlet:param name="categoryId" value="<%= String.valueOf(categoryId) %>" />
</portlet:actionURL>

<div class="lfr-attachment-cover-image-selector">
	<liferay-item-selector:image-selector
		draggableImage="vertical"
		fileEntryId='<%= BeanParamUtil.getLong(cpAttachmentFileEntry, request, "fileEntryId") %>'
		itemSelectorEventName="addCategoryCPAttachmentFileEntry"
		itemSelectorURL="<%= categoryCPAttachmentFileEntriesDisplayContext.getItemSelectorUrl() %>"
		maxFileSize="<%= categoryCPAttachmentFileEntriesDisplayContext.getImageMaxSize() %>"
		paramName="fileEntry"
		uploadURL="<%= uploadCoverImageURL %>"
		validExtensions="<%= StringUtil.merge(categoryCPAttachmentFileEntriesDisplayContext.getImageExtensions(), StringPool.COMMA_AND_SPACE) %>"
	/>
</div>

<aui:input name="title" />

<aui:input name="priority" />