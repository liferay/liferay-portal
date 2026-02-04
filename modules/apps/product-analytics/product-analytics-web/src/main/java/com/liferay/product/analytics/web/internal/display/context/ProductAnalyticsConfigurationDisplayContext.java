/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.analytics.web.internal.display.context;

import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;

import jakarta.servlet.http.HttpServletRequest;

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

	public boolean getEnabled() {
		return productAnalyticsConfiguration.enabled();
	}

}