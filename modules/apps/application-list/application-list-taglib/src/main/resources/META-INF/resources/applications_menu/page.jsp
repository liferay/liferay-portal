<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/applications_menu/init.jsp" %>

<%
ApplicationsMenuDisplayContext applicationsMenuDisplayContext = new ApplicationsMenuDisplayContext(request);
%>

<div>
	<div class="applications-menu-sidebar-wrapper <%= applicationsMenuDisplayContext.isVisible() ? "visible" : "hidden" %>">
		<div class="applications-menu-sidebar">
			<div class="align-items-center c-px-3 c-py-4 d-flex flex-row">
				<div>
					<%-- TODO: replace the icon below with the panel icon --%>
					<clay:icon
						symbol="grid"
					/>
				</div>

				<div class="c-px-2 flex-grow-1 text-4 text-weight-bold">
					<%= applicationsMenuDisplayContext.getPanelCategoryLabel() %>
				</div>

				<button class="close lfr-portal-tooltip rounded-lg" title="<%= LanguageUtil.get(request, "close-product-menu") %>" type="button">
					<clay:icon
						symbol="times"
					/>
				</button>
			</div>

			<div class="applications-menu-sidebar-body">
				<clay:vertical-nav
					active="<%= applicationsMenuDisplayContext.getPortletId() %>"
					defaultExpandedKeys="<%= applicationsMenuDisplayContext.getExpandedKeys() %>"
					verticalNavItems="<%= applicationsMenuDisplayContext.getVerticalNavItems() %>"
				/>
			</div>
		</div>
	</div>

	<react:component
		module="{ApplicationsMenu} from application-list-taglib"
		props="<%= applicationsMenuDisplayContext.getProps() %>"
	/>
</div>