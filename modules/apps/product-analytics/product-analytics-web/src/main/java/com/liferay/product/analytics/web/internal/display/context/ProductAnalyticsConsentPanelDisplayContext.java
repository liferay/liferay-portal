/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.analytics.web.internal.display.context;

import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Christopher Kian
 */
public class ProductAnalyticsConsentPanelDisplayContext
	extends BaseDisplayContext {

	public ProductAnalyticsConsentPanelDisplayContext(
		LayoutUtilityPageEntryLayoutProvider
			layoutUtilityPageEntryLayoutProvider,
		HttpServletRequest httpServletRequest) {

		super(httpServletRequest, layoutUtilityPageEntryLayoutProvider);
	}

	public Map<String, Object> getContext() {
		return HashMapBuilder.<String, Object>put(
			"optionalConsentCookieTypeNames",
			getConsentCookieTypeNamesJSONArray(getOptionalConsentCookieTypes())
		).put(
			"requiredConsentCookieTypeNames",
			getConsentCookieTypeNamesJSONArray(getRequiredConsentCookieTypes())
		).put(
			"showButtons", isShowButtons()
		).build();
	}

	public boolean isShowButtons() {
		HttpServletRequest httpServletRequest = getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return !themeDisplay.isStatePopUp();
	}

}