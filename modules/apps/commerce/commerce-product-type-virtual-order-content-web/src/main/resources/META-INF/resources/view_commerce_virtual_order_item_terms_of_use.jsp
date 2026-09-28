<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceVirtualOrderItemContentDisplayContext commerceVirtualOrderItemContentDisplayContext = (CommerceVirtualOrderItemContentDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

JournalArticleDisplay articleDisplay = commerceVirtualOrderItemContentDisplayContext.getArticleDisplay();
%>

<div class="journal-article-preview p-3">
	<c:choose>
		<c:when test="<%= articleDisplay != null %>">
			<%= articleDisplay.getContent() %>
		</c:when>
		<c:otherwise>
			<%= commerceVirtualOrderItemContentDisplayContext.getTermsOfUseContent() %>
		</c:otherwise>
	</c:choose>
</div>