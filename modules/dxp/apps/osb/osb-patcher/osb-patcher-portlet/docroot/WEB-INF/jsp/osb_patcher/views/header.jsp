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

<c:if test="${not empty param.title}">
	<c:if test="${empty param.redirect}">
		<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
			<portlet:renderURL var="backURL">
				<portlet:param name="controller" value="${param.controller}" />
				<portlet:param name="action" value="${param.action}" />
			</portlet:renderURL>
		</c:if>
	</c:if>

	<liferay-ui:header
		backURL="${backURL}"
		title="${param.title}"
	/>
</c:if>