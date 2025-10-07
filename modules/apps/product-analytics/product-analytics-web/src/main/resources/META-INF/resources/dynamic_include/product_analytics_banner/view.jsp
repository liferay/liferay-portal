<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<div aria-label="analytics banner product" class="product-analytics-banner product-analytics-banner-bottom" role="dialog">
	<liferay-portlet:runtime
		portletName="<%= ProductAnalyticsPortletKeys.PRODUCT_ANALYTICS_BANNER %>"
	/>
</div>