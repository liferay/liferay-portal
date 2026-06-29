<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewDashboardDisplayContext viewDashboardDisplayContext = (ViewDashboardDisplayContext)request.getAttribute(ViewDashboardDisplayContext.class.getName());
%>

<div class="cms-section">
	<react:component
		module="{Dashboards} from site-cms-site-initializer"
		props="<%= viewDashboardDisplayContext.getReactData() %>"
	/>
</div>