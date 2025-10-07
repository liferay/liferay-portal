<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<liferay-ui:header
	backURL='<%= (BrowserSnifferUtil.isChrome(request) && windowState.equals(LiferayWindowState.POP_UP)) ? request.getHeader(HttpHeaders.REFERER) : "javascript:history.go(-1);" %>'
	title="error"
/>

<liferay-ui:error exception="<%= PortalException.class %>">

	<%
	PortalException portalException = (PortalException)errorException;
	%>

	<c:choose>
		<c:when test="<%= Validator.isNotNull(portalException.getMessage()) %>">
			<liferay-ui:message key="<%= portalException.getMessage() %>" />
		</c:when>
		<c:otherwise>
			<liferay-ui:message key="an-unexpected-error-occurred" />
		</c:otherwise>
	</c:choose>
</liferay-ui:error>