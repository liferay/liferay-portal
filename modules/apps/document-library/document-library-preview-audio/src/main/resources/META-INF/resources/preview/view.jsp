<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/preview/init.jsp" %>

<%
List<String> previewFileURLs = (List<String>)request.getAttribute(DLPreviewAudioWebKeys.PREVIEW_FILE_URLS);
%>

<liferay-util:html-top
	outputKey="com.liferay.document.library.preview.audio#/preview/view.jsp"
>
	<aui:link hashedFile="<%= true %>" href="document-library-preview-css/css/main.css" rel="stylesheet" />
</liferay-util:html-top>

<div class="preview-file">
	<div class="preview-file-container">
		<audio
			class="preview-file-audio"
			controls
			controlsList="nodownload"
			style="max-width: <%= PropsValues.DL_FILE_ENTRY_PREVIEW_VIDEO_WIDTH %>px;"
		>

			<%
			for (String previewFileURL : previewFileURLs) {
				String type = null;

				if (Validator.isNotNull(previewFileURL)) {
					if (previewFileURL.endsWith("mp3")) {
						type = "audio/mp3";
					}
					else if (previewFileURL.endsWith("ogg")) {
						type = "audio/ogg";
					}
				}
			%>

				<c:if test="<%= type != null %>">
					<source src="<%= previewFileURL %>" type="<%= type %>" />
				</c:if>

			<%
			}
			%>

		</audio>
	</div>
</div>