<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/application_content/init.jsp" %>

<%
PanelAppContentHelper panelAppContentHelper = new PanelAppContentHelper(request, response);

boolean applicationsMenuEnabled = true;
%>

<c:if test="<%= applicationsMenuEnabled %>">
	<div class="d-flex">
		<%@ include file="/applications_menu/page.jsp" %>

		<div class="flex-grow-1">
</c:if>

<c:choose>
	<c:when test="<%= panelAppContentHelper.isValidPortletSelected() %>">

		<%
		panelAppContentHelper.writeContent(pageContext.getOut());
		%>

	</c:when>
	<c:otherwise>
		<div class="portlet-msg-info">
			<liferay-ui:message key="please-select-a-tool-from-the-left-menu" />
		</div>
	</c:otherwise>
</c:choose>

<c:if test="<%= applicationsMenuEnabled %>">
		</div>
	</div>
</c:if>