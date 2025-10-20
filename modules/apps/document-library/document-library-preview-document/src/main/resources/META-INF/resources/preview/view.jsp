<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/preview/init.jsp" %>

<%
String randomNamespace = PortalUtil.generateRandomKey(request, "portlet_document_library_view_file_entry_preview") + StringPool.UNDERLINE;

FileVersion fileVersion = (FileVersion)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_VERSION);

int previewFileCount = PDFProcessorUtil.getPreviewFileCount(fileVersion);

String previewQueryString = "&previewFileIndex=1";

int status = ParamUtil.getInteger(request, "status", WorkflowConstants.STATUS_ANY);

if (status != WorkflowConstants.STATUS_ANY) {
	previewQueryString += "&status=" + status;
}

String[] previewFileURLs = new String[1];

previewFileURLs[0] = DLURLHelperUtil.getPreviewURL(fileVersion.getFileEntry(), fileVersion, themeDisplay, previewQueryString);

String previewFileURL = previewFileURLs[0];
%>

<liferay-util:html-top
	outputKey="com.liferay.document.library.preview.document#/preview/view.jsp"
>
	<aui:link hashedFile="<%= true %>" href="document-library-preview-css/css/main.css" rel="stylesheet" />
</liferay-util:html-top>

<clay:stripe
	dismissible="<%= true %>"
	displayType="info"
	message="the-document-preview-may-not-show-all-pages"
/>

<div id="<portlet:namespace /><%= randomNamespace %>previewDocument">
	<react:component
		module="{DocumentPreviewer} from document-library-preview-document"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"alt", fileVersion.getDescription()
			).put(
				"baseImageURL", previewFileURL
			).put(
				"initialPage", 1
			).put(
				"totalPages", previewFileCount
			).build()
		%>'
	/>
</div>