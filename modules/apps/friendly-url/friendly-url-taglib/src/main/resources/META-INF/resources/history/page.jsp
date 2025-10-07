<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/history/init.jsp" %>

<%
String defaultLanguageId = (String)request.getAttribute("liferay-friendly-url:history:defaultLanguageId");
boolean disabled = (boolean)request.getAttribute("liferay-friendly-url:history:disabled");
String elementId = (String)request.getAttribute("liferay-friendly-url:history:elementId");
String friendlyURLEntryURL = (String)request.getAttribute("liferay-friendly-url:history:friendlyURLEntryURL");
boolean localizable = (boolean)request.getAttribute("liferay-friendly-url:history:localizable");
%>

<liferay-util:html-top
	outputKey="com.liferay.friendly.url.taglib#/history/page.jsp"
>
	<aui:link hashedFile="<%= true %>" href="friendly-url-taglib/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div class="btn-url-history-wrapper">
	<react:component
		module="{FriendlyURLHistory} from friendly-url-taglib"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"defaultLanguageId", defaultLanguageId
			).put(
				"disabled", disabled
			).put(
				"elementId", elementId
			).put(
				"friendlyURLEntryURL", friendlyURLEntryURL
			).put(
				"localizable", localizable
			).build()
		%>'
	/>
</div>