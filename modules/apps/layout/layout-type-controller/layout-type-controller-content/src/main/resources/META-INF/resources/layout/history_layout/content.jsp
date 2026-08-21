<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/layout/history_layout/init.jsp" %>

<div class="layout-content-version">
	<liferay-portlet:runtime
		portletName="<%= LayoutContentVersionPortletKeys.LAYOUT_CONTENT_VERSION %>"
	/>
</div>

<liferay-layout:layout-common />