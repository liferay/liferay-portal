<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/portlet/init.jsp" %>

<%
ProductMenuSidebarDisplayContext productMenuSidebarDisplayContext = new ProductMenuSidebarDisplayContext(request);
%>

<div class="cadmin d-print-none lfr-product-menu-panel product-menu-sidebar" id="com_liferay_product_navigation_product_menu_web_sidebar_container">
	<c:if test="<%= productMenuSidebarDisplayContext.isVisible() %>">
		<div class="c-slideout c-slideout-fixed c-slideout-height-full c-slideout-push c-slideout-shown c-slideout-start">
			<section class="c-slideout-show sidebar sidebar-light skeleton" data-qa-id="sideNavigation" data-testid="sideNavigation">
				<svg class="product-menu-sidebar-skeleton">
					<use href="<%= String.format("%s/skeletons/homes_side_navigation.svg#homes-side-navigation", themeDisplay.getPathThemeImages()) %>" />
				</svg>
			</section>
		</div>
	</c:if>

	<react:component
		module="{ProductMenuSidebar} from product-navigation-product-menu-web"
		props="<%= productMenuSidebarDisplayContext.getProps() %>"
	/>
</div>