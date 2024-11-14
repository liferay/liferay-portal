<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SampleDisplayContext sampleDisplayContext = (SampleDisplayContext)request.getAttribute(SampleWebKeys.SAMPLE_DISPLAY_CONTEXT);
%>

<clay:navigation-bar
	navigationItems="<%= sampleDisplayContext.getNavigationItems() %>"
/>

<%
String navigation = ParamUtil.getString(request, "navigation", "fieldset");
%>

<c:choose>
	<c:when test='<%= navigation.equals("fieldset") %>'>
		<liferay-util:include page="/partials/fieldset.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= navigation.equals("input-localized") %>'>
		<liferay-util:include page="/partials/input_localized.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= navigation.equals("logo-selector") %>'>
		<liferay-util:include page="/partials/logo_selector.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= navigation.equals("search-iterator") %>'>
		<liferay-util:include page="/partials/search_iterator.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:otherwise>
		<liferay-util:include page="/partials/search_paginator.jsp" servletContext="<%= application %>" />
	</c:otherwise>
</c:choose>