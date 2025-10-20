<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ItemSelectorUploadViewDisplayContext itemSelectorUploadViewDisplayContext = (ItemSelectorUploadViewDisplayContext)request.getAttribute(ItemSelectorUploadView.ITEM_SELECTOR_UPLOAD_VIEW_DISPLAY_CONTEXT);

ItemSelectorReturnTypeResolver<?, ?> itemSelectorReturnTypeResolver = itemSelectorUploadViewDisplayContext.getItemSelectorReturnTypeResolver();

Class<?> itemSelectorReturnTypeClass = itemSelectorReturnTypeResolver.getItemSelectorReturnTypeClass();

String uploadURL = itemSelectorUploadViewDisplayContext.getURL();

String namespace = itemSelectorUploadViewDisplayContext.getNamespace();

if (Validator.isNotNull(namespace)) {
	uploadURL = HttpComponentsUtil.addParameter(uploadURL, namespace + "returnType", itemSelectorReturnTypeClass.getName());
}
%>

<clay:container-fluid
	cssClass="lfr-item-viewer"
	id="itemSelectorUploadContainer"
>
	<liferay-util:html-top
		outputKey="com.liferay.item.selector.upload.web#/upload.jsp"
	>
		<aui:link hashedFile="<%= true %>" href="item-selector-taglib/repository_entry_browser/css/main.css" rel="stylesheet" />
	</liferay-util:html-top>

	<div class="dropzone-wrapper dropzone-wrapper-search-container-empty">
		<div class="dropzone dropzone-disabled"><span aria-hidden="true" class="loading-animation loading-animation-sm"></span></div>

		<react:component
			module="{ItemSelectorRepositoryEntryBrowser} from item-selector-upload-web"
			props='<%=
				HashMapBuilder.<String, Object>put(
					"closeCaption", itemSelectorUploadViewDisplayContext.getTitle(locale)
				).put(
					"editImageURL", uploadURL
				).put(
					"itemSelectedEventName", itemSelectorUploadViewDisplayContext.getItemSelectedEventName()
				).put(
					"maxFileSize", itemSelectorUploadViewDisplayContext.getMaxFileSize()
				).put(
					"mimeTypeRestriction", itemSelectorUploadViewDisplayContext.getMimeTypeRestriction()
				).put(
					"rootNode", "#itemSelectorUploadContainer"
				).put(
					"uploadItemReturnType", HtmlUtil.escapeAttribute(itemSelectorReturnTypeClass.getName())
				).put(
					"uploadItemURL", uploadURL
				).put(
					"validExtensions", StringUtil.merge(itemSelectorUploadViewDisplayContext.getExtensions())
				).build()
			%>'
		/>
	</div>
</clay:container-fluid>