<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<%

// According to http://www.webmasterworld.com/forum91/3087.htm a semicolon in
// the URL for a meta-refresh tag does not work in IE 6.

// To work around this issue, we use a URL without a session id for meta-refresh
// and rely on the load event on the body element to properly rewrite the URL.

// However, if the original request was an AJAX request, sending a redirect is
// less than ideal. In this case we will simply print the error message.

ErrorData errorData = pageContext.getErrorData();

int code = errorData.getStatusCode();

String msg = String.valueOf(request.getAttribute(JavaConstants.JAKARTA_SERVLET_ERROR_MESSAGE));
String uri = errorData.getRequestURI();

String xRequestWith = request.getHeader(HttpHeaders.X_REQUESTED_WITH);
%>

<html>
	<c:choose>
		<c:when test="<%= !StringUtil.equalsIgnoreCase(HttpHeaders.XML_HTTP_REQUEST, xRequestWith) %>">

			<%
			String redirect = null;

			LayoutSet layoutSet = (LayoutSet)request.getAttribute(WebKeys.VIRTUAL_HOST_LAYOUT_SET);

			if (layoutSet != null) {
				redirect = PortalUtil.getPathMain();
			}
			else {
				redirect = PortalUtil.getPortalURL(PortalUtil.getValidPortalDomain(PortalUtil.getDefaultCompanyId(), request.getServerName()), request.getServerPort(), request.isSecure()) + PortalUtil.getPathContext() + PortalUtil.getRelativeHomeURL(request);
			}

			if (!request.isRequestedSessionIdFromCookie()) {
				redirect = PortalUtil.getURLWithSessionId(redirect, session.getId());
			}
			%>

			<head>
				<title></title>

				<meta content="1; url=<%= HtmlUtil.escapeAttribute(redirect) %>" http-equiv="refresh" />
			</head>

			<liferay-ui:csp>
				<body onload="javascript:location.replace('<%= HtmlUtil.escapeJS(redirect) %>')">

					<!--
					The numbers below are used to fill up space so that this works properly in IE.
					See http://support.microsoft.com/default.aspx?scid=kb;en-us;Q294807 for more
					information on why this is necessary.

					12345678901234567890123456789012345678901234567890123456789012345678901234567890
					12345678901234567890123456789012345678901234567890123456789012345678901234567890
					12345678901234567890123456789012345678901234567890123456789012345678901234567890
					-->
				</body>
			</liferay-ui:csp>
		</c:when>
		<c:otherwise>
			<head>
				<title>Http Status <%= code %> - <liferay-ui:message key='<%= "http-status-code[" + code + "]" %>' /></title>
			</head>

			<body>
				<h1>Http Status <%= code %> - <liferay-ui:message key='<%= "http-status-code[" + code + "]" %>' /></h1>

				<p>
					<liferay-ui:message key="message" />: <%= HtmlUtil.escape(msg) %>
				</p>

				<p>
					<liferay-ui:message key="resource" />: <%= HtmlUtil.escape(uri) %>
				</p>
			</body>
		</c:otherwise>
	</c:choose>
</html>