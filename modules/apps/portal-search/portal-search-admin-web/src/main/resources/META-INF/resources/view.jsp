<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.admin.web.internal.display.context.SearchAdminDisplayContext" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
SearchAdminDisplayContext searchAdminDisplayContext = (SearchAdminDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

String selectedTab = searchAdminDisplayContext.getSelectedTab();
%>

<clay:navigation-bar
	navigationItems="<%= searchAdminDisplayContext.getNavigationItemList() %>"
/>

<c:choose>
	<c:when test='<%= selectedTab.equals("connections") %>'>
		<liferay-util:include page="/connections.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= selectedTab.equals("field-mappings") %>'>
		<liferay-util:include page="/field_mappings.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= selectedTab.equals("index-actions") %>'>
		<liferay-util:include page="/index_actions.jsp" servletContext="<%= application %>" />
	</c:when>
</c:choose>