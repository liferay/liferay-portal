<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

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