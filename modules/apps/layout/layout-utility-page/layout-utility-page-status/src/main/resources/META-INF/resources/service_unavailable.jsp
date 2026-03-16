<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-layout:render-layout-utility-page-entry
	type="<%= LayoutUtilityPageEntryConstants.TYPE_SC_SERVICE_UNAVAILABLE %>"
>
	<clay:alert
		displayType="warning"
		message="503-service-unavailable"
	/>

	<liferay-ui:message key="this-site-is-temporarily-unavailable-for-maintenance" />
</liferay-layout:render-layout-utility-page-entry>