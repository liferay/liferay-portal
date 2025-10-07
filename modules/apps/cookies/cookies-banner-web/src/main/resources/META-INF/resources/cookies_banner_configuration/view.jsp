<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CookiesBannerConfigurationDisplayContext cookiesBannerConfigurationDisplayContext = (CookiesBannerConfigurationDisplayContext)request.getAttribute(CookiesBannerWebKeys.COOKIES_BANNER_CONFIGURATION_DISPLAY_CONTEXT);
%>

<clay:container-fluid
	cssClass="container-view p-md-4"
	id='<%= liferayPortletResponse.getNamespace() + "cookiesBannerConfigurationForm" %>'
>
	<c:choose>
		<c:when test="<%= cookiesBannerConfigurationDisplayContext.isShowButtons() %>">
			<clay:sheet>
				<clay:sheet-header>
					<h2 class="sheet-title">
						<liferay-ui:message key="cookie-preference-handling-configuration-name" />
					</h2>
				</clay:sheet-header>

				<liferay-util:include page="/cookies_banner_configuration/cookies_banner_configuration.jsp" servletContext="<%= application %>" />
			</clay:sheet>
		</c:when>
		<c:otherwise>
			<liferay-util:include page="/cookies_banner_configuration/cookies_banner_configuration.jsp" servletContext="<%= application %>" />
		</c:otherwise>
	</c:choose>
</clay:container-fluid>

<liferay-frontend:component
	componentId="CookiesBannerConfiguration"
	context="<%= cookiesBannerConfigurationDisplayContext.getContext() %>"
	module="{CookiesBannerConfiguration} from cookies-banner-web"
/>