<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<clay:navigation-bar
	navigationItems="<%= fipsAdminDisplayContext.getNavigationItems() %>"
/>

<div class="container-fluid container-fluid-max-xl mt-3">
	<liferay-util:include page="/session_configuration.jsp" servletContext="<%= application %>" />
</div>