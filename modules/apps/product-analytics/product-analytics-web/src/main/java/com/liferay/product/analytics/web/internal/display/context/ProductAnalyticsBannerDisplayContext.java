/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.analytics.web.internal.display.context;

import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.product.analytics.web.internal.constants.ProductAnalyticsPortletKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;

/**
 * @author Christopher Kian
 */
public class ProductAnalyticsBannerDisplayContext extends BaseDisplayContext {

	public ProductAnalyticsBannerDisplayContext(
		HttpServletRequest httpServletRequest,
		LayoutUtilityPageEntryLayoutProvider
			layoutUtilityPageEntryLayoutProvider) {

		super(httpServletRequest, layoutUtilityPageEntryLayoutProvider);
	}

	public Map<String, Object> getContext(Locale locale) {
		return HashMapBuilder.<String, Object>put(
			"configurationNamespace",
			ProductAnalyticsPortletKeys.PRODUCT_ANALYTICS_CONSENT_PANEL
		).put(
			"configurationURL", _getConfigurationURL()
		).put(
			"optionalConsentCookieTypeNames",
			getConsentCookieTypeNamesJSONArray(getOptionalConsentCookieTypes())
		).put(
			"requiredConsentCookieTypeNames",
			getConsentCookieTypeNamesJSONArray(getRequiredConsentCookieTypes())
		).put(
			"title",
			LanguageUtil.get(locale, "product-analytics-consent-panel-title")
		).build();
	}

	private String _getConfigurationURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(getHttpServletRequest());

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createRenderURL(
				ProductAnalyticsPortletKeys.PRODUCT_ANALYTICS_CONSENT_PANEL)
		).setMVCPath(
			"/product_analytics_consent_panel/view.jsp"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

}