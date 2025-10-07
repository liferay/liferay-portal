<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewHomeQuickActionsDisplayContext viewHomeQuickActionsDisplayContext = (ViewHomeQuickActionsDisplayContext)request.getAttribute(ViewHomeQuickActionsDisplayContext.class.getName());
%>

<c:if test="<%= viewHomeQuickActionsDisplayContext.hasAddEntryPermission() %>">
	<div class="cms-section">
		<react:component
			module="{QuickActions} from site-cms-site-initializer"
			props="<%= viewHomeQuickActionsDisplayContext.getProps() %>"
		/>
	</div>
</c:if>