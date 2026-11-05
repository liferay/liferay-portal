<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SideNavigationDisplayContext sideNavigationDisplayContext = (SideNavigationDisplayContext)request.getAttribute(SideNavigationDisplayContext.class.getName());
%>

<div class="cadmin d-print-none lfr-product-menu-panel side-navigation-container side-navigation-fixed" id="com_liferay_application_list_taglib_side_navigation_container">
	<react:component
		module="{SideNavigation} from application-list-taglib"
		props="<%= sideNavigationDisplayContext.getProps() %>"
	/>
</div>