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

String previewQueryString = "&imagePreview=1";

int status = ParamUtil.getInteger(request, "status", WorkflowConstants.STATUS_ANY);

if (status != WorkflowConstants.STATUS_ANY) {
	previewQueryString += "&status=" + status;
}

String previewURL = DLURLHelperUtil.getPreviewURL(fileVersion.getFileEntry(), fileVersion, themeDisplay, previewQueryString);
%>

<liferay-util:html-top
	outputKey="com.liferay.document.library.preview.image#/preview/view.jsp"
>
	<aui:link hashedFile="<%= true %>" href="document-library-preview-image/preview/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<c:choose>
	<c:when test="<%= Objects.equals(fileVersion.getMimeType(), ContentTypes.IMAGE_SVG_XML) %>">
		<div class="preview-file">
			<div class="preview-file-container preview-file-max-height">
				<img alt="<%= HtmlUtil.escapeAttribute(fileVersion.getDescription()) %>" class="preview-file-image-vectorial" src="<%= previewURL %>" />
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div id="<portlet:namespace /><%= randomNamespace %>previewImage">
			<react:component
				module="{ImagePreviewer} from document-library-preview-image"
				props='<%=
					HashMapBuilder.<String, Object>put(
						"alt", fileVersion.getDescription()
					).put(
						"imageURL", previewURL
					).build()
				%>'
			/>
		</div>
	</c:otherwise>
</c:choose>