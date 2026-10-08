<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String nonceAttribute = ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(request);

if (themeDisplay.isSignedIn()) {
%>

	<link href="<%= HtmlUtil.escapeAttribute(PortalUtil.getStaticResourceURL(request, "/o/osb-faro-web/css/fonts.css")) %>" <%= nonceAttribute %> rel="stylesheet" type="text/css" />
	<link href="<%= HtmlUtil.escapeAttribute(PortalUtil.getStaticResourceURL(request, "/o/osb-faro-web/dist/main.css")) %>" <%= nonceAttribute %> rel="stylesheet" type="text/css" />

	<div id="faroApp"></div>

	<script defer <%= nonceAttribute %> src="<%= HtmlUtil.escapeAttribute(PortalUtil.getStaticResourceURL(request, "/o/osb-faro-web/dist/main.js")) %>" type="module"></script>

	<script <%= nonceAttribute %>>
		document.addEventListener('DOMContentLoaded', () => {
			document.querySelector('#liferayAUICSS')?.remove();
			document.querySelector('#liferayThemeCSS')?.remove();
		});
	</script>

<%
}
else {
%>

	<script <%= nonceAttribute %>>
		window.location.href = '<%= HtmlUtil.escapeJS(themeDisplay.getURLSignIn() + "?redirect=" + URLCodec.encodeURL(PortalUtil.getCurrentURL(request))) %>';
	</script>

<%
}
%>
