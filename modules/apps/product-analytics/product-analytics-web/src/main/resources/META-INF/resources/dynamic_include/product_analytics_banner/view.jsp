<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<%
ProductAnalyticsConfigurationDisplayContext productAnalyticsConfigurationDisplayContext = (ProductAnalyticsConfigurationDisplayContext)request.getAttribute(ProductAnalyticsWebKeys.PRODUCT_ANALYTICS_CONFIGURATION_DISPLAY_CONTEXT);
%>

<aui:script src="https://storage.googleapis.com/liferaycloud-cdn-product-experience-manager-assets-prd/self-hosted-script/product-analytics-script.umd.min.js" type="text/javascript"></aui:script>

<aui:script type="module">
	const lastModified =
		'<%= productAnalyticsConfigurationDisplayContext.getLastModified() %>';

	if (
		Liferay.Util.Cookie.get(
			'PRODUCT_ANALYTICS_CONFIGURED_DATE',
			Liferay.Util.Cookie.TYPES.NECESSARY
		) !== lastModified
	) {
		[
			'PRODUCT_ANALYTICS_CONFIGURED',
			'PRODUCT_ANALYTICS_CONFIGURED_DATE',
			'PRODUCT_ANALYTICS_CONSENT_TYPE_FUNCTIONAL',
			'PRODUCT_ANALYTICS_CONSENT_TYPE_NECESSARY',
			'PRODUCT_ANALYTICS_CONSENT_TYPE_PERFORMANCE',
			'PRODUCT_ANALYTICS_CONSENT_TYPE_PERSONALIZATION',
			'PRODUCT_ANALYTICS_CONSENT_TYPE_PRODUCT_ANALYTICS',
		].forEach((cookieName) => Liferay.Util.Cookie.remove(cookieName));
	}

	window.productAnalyticsScript = new window.productAnalyticsScript({
		consentRenewalPeriod:
			'<%= productAnalyticsConfigurationDisplayContext.getConsentRenewalPeriod() %>',
		lastModified,
	});

	window.productAnalyticsScript.startTrackingJourney();

	Liferay.on('endNavigate', () => {
		window.productAnalyticsScript.dispose();

		window.productAnalyticsScript.startTrackingJourney();
	});
</aui:script>