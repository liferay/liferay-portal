<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/WEB-INF/jsp/osb_patcher/views/init.jsp" %>

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