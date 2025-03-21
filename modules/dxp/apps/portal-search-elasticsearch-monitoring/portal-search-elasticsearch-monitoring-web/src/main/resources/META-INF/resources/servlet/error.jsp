<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.security.auth.PrincipalException" %><%@
page import="com.liferay.portal.search.elasticsearch.monitoring.web.internal.constants.MonitoringProxyServletWebKeys" %><%@
page import="com.liferay.portal.search.elasticsearch.monitoring.web.internal.servlet.display.context.ErrorDisplayContext" %>

<%@ page import="java.net.ConnectException" %>

<%
ErrorDisplayContext errorDisplayContext = (ErrorDisplayContext)request.getAttribute(MonitoringProxyServletWebKeys.ERROR_DISPLAY_CONTEXT);

Exception e = errorDisplayContext.getException();
%>

<div>
	<liferay-ui:message arguments="monitoring-configuration-name[elasticsearch]" key="is-temporarily-unavailable" translateArguments="<%= true %>" />
</div>

<div>
	<c:if test="<%= e instanceof ConnectException %>">
		<liferay-ui:message arguments="<%= errorDisplayContext.getConnectExceptionAddress() %>" key="could-not-connect-to-address-x.-please-verify-that-the-specified-port-is-correct-and-that-the-remote-server-is-configured-to-accept-requests-from-this-server" />
	</c:if>

	<c:if test="<%= e instanceof PrincipalException.MustBeAuthenticated %>">
		<liferay-ui:message key="please-sign-in-to-access-this-application" />
	</c:if>

	<c:if test="<%= e instanceof PrincipalException.MustHavePermission %>">
		<liferay-ui:message key="you-do-not-have-the-required-permissions" />
	</c:if>
</div>