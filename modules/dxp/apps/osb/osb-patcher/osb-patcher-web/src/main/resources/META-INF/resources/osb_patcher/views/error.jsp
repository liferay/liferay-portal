<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<c:set value='<%= (BrowserSnifferUtil.isChrome(request) && windowState.equals(LiferayWindowState.POP_UP)) ? request.getHeader(HttpHeaders.REFERER) : "javascript:history.go(-1);" %>' var="backURL" />

<liferay-ui:header
	backURL="${backURL}"
	title="error"
/>

<c:choose>
	<c:when test="${fn:length(pattern) > 0}">
		<liferay-ui:message arguments="${arguments}" key="${pattern}" />
	</c:when>
	<c:otherwise>
		<liferay-ui:message key="an-unexpected-error-occurred" />
	</c:otherwise>
</c:choose>