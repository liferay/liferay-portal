<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-util:html-top
	outputKey="com.liferay.document.library.video#/embed/error.jsp"
>
	<aui:link hashedFile="<%= true %>" href="document-library-video/css/embed.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div class="video-embed-placeholder">
	<clay:icon
		symbol="video"
	/>

	<div class="video-embed-placeholder-text">
		<liferay-ui:message key="no-video-preview-available" />
	</div>
</div>