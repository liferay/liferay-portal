<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/portlet/init.jsp" %>

<c:choose>
	<c:when test='<%= FeatureFlagManagerUtil.isEnabled(themeDisplay.getCompanyId(), "LPD-88120") %>'>
		<liferay-util:include page="/portlet/product_menu_sidebar.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:otherwise>

		<%
		String pagesTreeState = SessionClicks.get(request, "com.liferay.product.navigation.product.menu.web_pagesTreeState", "closed");
		String productMenuState = SessionClicks.get(request, "com.liferay.product.navigation.product.menu.web_productMenuState", "closed");

		ProductMenuDisplayContext productMenuDisplayContext = new ProductMenuDisplayContext(liferayPortletRequest);
		%>

		<nav aria-label="<%= LanguageUtil.get(request, "product-menu") %>" class="<%= productMenuState %><%= Objects.equals(productMenuState, "open") ? " product-menu-open" : StringPool.BLANK %> cadmin d-print-none lfr-product-menu-panel sidenav-fixed sidenav-menu-slider" id="<portlet:namespace />sidenavSliderId" tabindex="-1">
			<div class="product-menu sidebar sidenav-menu">
				<div class="lfr-applications-menu lfr-product-menu-sidebar" id="productMenuSidebar">
					<div class="sidebar-body">
						<c:choose>
							<c:when test='<%= Objects.equals(productMenuState, "open") && (!Objects.equals(pagesTreeState, "open") || productMenuDisplayContext.isLayoutsTreeDisabled() || !productMenuDisplayContext.isShowLayoutsTree()) %>'>
								<liferay-util:include page="/portlet/product_menu.jsp" servletContext="<%= application %>" />
							</c:when>
							<c:when test='<%= Objects.equals(productMenuState, "open") && Objects.equals(pagesTreeState, "open") %>'>
								<div class="pages-tree">
									<liferay-util:include page="/portlet/pages_tree.jsp" servletContext="<%= application %>">
										<liferay-util:param name="redirect" value="<%= themeDisplay.getURLCurrent() %>" />
									</liferay-util:include>
								</div>
							</c:when>
						</c:choose>
					</div>
				</div>
			</div>
		</nav>
	</c:otherwise>
</c:choose>