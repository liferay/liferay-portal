<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/control/menu/init.jsp" %>

<%
LayoutsAdminDisplayContext layoutsAdminDisplayContext = (LayoutsAdminDisplayContext)request.getAttribute(LayoutAdminWebKeys.LAYOUT_PAGE_LAYOUT_ADMIN_DISPLAY_CONTEXT);
%>

<li class="control-menu-nav-item">
	<react:component
		module="{LayoutAIAssistantTriggerButton} from layout-admin-web"
		props="<%= layoutsAdminDisplayContext.getAIAssistantProps() %>"
	/>
</li>