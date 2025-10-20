<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
List<String> previewFileURLs = (List<String>)request.getAttribute(DLVideoWebKeys.PREVIEW_FILE_URLS);
String videoPosterURL = (String)request.getAttribute(DLVideoWebKeys.VIDEO_POSTER_URL);
%>

<liferay-util:html-top
	outputKey="com.liferay.document.library.video#/embed/video.jsp"
>
	<aui:link hashedFile="<%= true %>" href="document-library-video/css/embed.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<video
	controls
	controlsList="nodownload"

	<c:if test="<%= Validator.isNotNull(videoPosterURL) %>">
		poster="<%= videoPosterURL %>"
	</c:if>
>

	<%
	for (String previewFileURL : previewFileURLs) {
		String type = null;

		if (Validator.isNotNull(previewFileURL)) {
			if (previewFileURL.endsWith("mp4")) {
				type = "video/mp4";
			}
			else if (previewFileURL.endsWith("ogv")) {
				type = "video/ogv";
			}
		}
	%>

		<c:if test="<%= type != null %>">
			<source src="<%= previewFileURL %>" type="<%= type %>" />
		</c:if>

	<%
	}
	%>

</video>