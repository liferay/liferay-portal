<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<c:choose>
	<c:when test="<%= themeDisplay.isSignedIn() %>">
		<aui:link href='<%= PortalUtil.getStaticResourceURL(request, "/o/osb-faro-web/css/fonts.css") %>' rel="stylesheet" type="text/css" />
		<aui:link href='<%= PortalUtil.getStaticResourceURL(request, "/o/osb-faro-web/dist/main.css") %>' rel="stylesheet" type="text/css" />

		<div id="faroApp"></div>

		<aui:script src='<%= PortalUtil.getStaticResourceURL(request, "/o/osb-faro-web/dist/main.js") %>' type="module"></aui:script>

		<aui:script>
			document.addEventListener('DOMContentLoaded', () => {
				document.querySelector('#liferayAUICSS')?.remove();
				document.querySelector('#liferayThemeCSS')?.remove();
			});
		</aui:script>
	</c:when>
	<c:otherwise>
		<aui:script>
			window.location.href =
				'<%= HtmlUtil.escapeJS(themeDisplay.getURLSignIn() + "?redirect=" + URLCodec.encodeURL(PortalUtil.getCurrentURL(request))) %>';
		</aui:script>
	</c:otherwise>
</c:choose>