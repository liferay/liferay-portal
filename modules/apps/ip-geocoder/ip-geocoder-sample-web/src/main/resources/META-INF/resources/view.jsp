<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.ip.geocoder.IPInfo" %>

<%
IPInfo ipInfo = (IPInfo)request.getAttribute(IPInfo.class.getName());
%>

<c:choose>
	<c:when test="<%= ipInfo != null %>">
		Country Code: <%= ipInfo.getCountryCode() %><br />
		IP Address: <%= ipInfo.getIPAddress() %><br />
	</c:when>
	<c:otherwise>
		<div class="alert alert-error">
			<liferay-ui:message key="an-unexpected-error-occurred" />
		</div>
	</c:otherwise>
</c:choose>