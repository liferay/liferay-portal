<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<div class="align-items-center d-flex">
	<a class="btn btn-monospaced btn-sm btn-unstyled mr-2" href="<%= FaroNavigationUtil.fetchLayoutURL("/sites", themeDisplay) %>" title="<liferay-ui:message key="back" />">
		<clay:icon
			symbol="angle-left"
		/>
	</a>

	<h1 class="cms-title component-title">
		<liferay-ui:message key="settings" />
	</h1>
</div>