<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/side_navigation/init.jsp" %>

<%
SideNavigationDisplayContext sideNavigationDisplayContext = new SideNavigationDisplayContext(request);
%>

<div class="side-navigation-container c-slideout-container<%= sideNavigationDisplayContext.isVisible() ? " c-slideout-push-start" : "" %>" id="com_liferay_application_list_taglib_side_navigation_container">
	<div class="c-slideout c-slideout-fixed c-slideout-push c-slideout-start<%= sideNavigationDisplayContext.isVisible() ? " c-slideout-shown" : "" %>">
		<section class="skeleton sidebar sidebar-light<%= sideNavigationDisplayContext.isVisible() ? " c-slideout-show" : "" %>" data-qa-id="sideNavigation" data-testid="sideNavigation">
			<svg class="side-navigation-skeleton">
				<use href="<%= String.format("%s/skeletons/homes_side_navigation.svg#homes-side-navigation", themeDisplay.getPathThemeImages()) %>" />
			</svg>
		</section>
	</div>

	<react:component
		module="{SideNavigation} from application-list-taglib"
		props="<%= sideNavigationDisplayContext.getProps() %>"
	/>
</div>