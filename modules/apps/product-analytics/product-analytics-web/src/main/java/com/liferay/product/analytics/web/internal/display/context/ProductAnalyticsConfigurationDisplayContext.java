/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.analytics.web.internal.display.context;

import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;

/**
 * @author Lucas Miranda
 */
public class ProductAnalyticsConfigurationDisplayContext
	extends BaseDisplayContext {

	public ProductAnalyticsConfigurationDisplayContext(
		HttpServletRequest httpServletRequest,
		LayoutUtilityPageEntryLayoutProvider
			layoutUtilityPageEntryLayoutProvider) {

		super(httpServletRequest, layoutUtilityPageEntryLayoutProvider);
	}

	public int getConsentRenewalPeriod() {
		return productAnalyticsConfiguration.consentRenewalPeriod();
	}

	public Map<String, Object> getContext(Locale locale) {
		return HashMapBuilder.<String, Object>put(
			"consentRenewalPeriod", getConsentRenewalPeriod()
		).put(
			"enabled", getEnabled()
		).put(
			"lastModified", getLastModified()
		).build();
	}

	public boolean getEnabled() {
		return productAnalyticsConfiguration.enabled();
	}

	public long getLastModified() {
		return productAnalyticsConfiguration.lastModified();
	}

}