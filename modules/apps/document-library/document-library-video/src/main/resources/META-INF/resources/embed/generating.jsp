<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-util:html-top
	outputKey="com.liferay.document.library.video#/embed/generating.jsp"
>
	<aui:link hashedFile="<%= true %>" href="document-library-video/css/embed.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div class="video-embed-placeholder">
	<span aria-hidden="true" class="loading-animation"></span>

	<div class="video-embed-placeholder-text">
		<liferay-ui:message key="generating-preview-will-take-a-few-minutes" />
	</div>
</div>

<%
FileVersion fileVersion = (FileVersion)request.getAttribute(FileVersion.class.getName());
%>

<portlet:resourceURL id="/document_library_video/get_embed_video_status" var="getEmbedVideoStatusURL">
	<portlet:param name="fileVersionId" value="<%= String.valueOf(fileVersion.getFileVersionId()) %>" />
</portlet:resourceURL>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"getEmbedVideoStatusURL", getEmbedVideoStatusURL
		).build()
	%>'
	module="{generating} from document-library-video"
/>