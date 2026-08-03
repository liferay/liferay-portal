<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-util:include page="/navigation.jsp" servletContext="<%= application %>" />

<%
ViewProfilesDisplayContext viewProfilesDisplayContext = new ViewProfilesDisplayContext(request, liferayPortletResponse);
%>

<frontend-data-set:headless-display
	apiURL="<%= viewProfilesDisplayContext.getAPIURL() %>"
	fdsSortItemList="<%= viewProfilesDisplayContext.getFDSSortItemList() %>"
	id="<%= viewProfilesDisplayContext.getFDSName() %>"
	style="fluid"
/>