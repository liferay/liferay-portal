<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
HomeDisplayContext homeDisplayContext = new HomeDisplayContext(request);
%>

<react:component
	module="{ApplicationsHome} from product-navigation-applications-menu-web"
	props="<%= homeDisplayContext.getProps() %>"
/>