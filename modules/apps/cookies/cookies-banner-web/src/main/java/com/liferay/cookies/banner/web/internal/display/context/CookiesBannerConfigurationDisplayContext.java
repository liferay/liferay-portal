/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.display.context;

import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;

/**
 * @author Eduardo García
 */
public class CookiesBannerConfigurationDisplayContext
	extends BaseCookiesBannerDisplayContext {

	public CookiesBannerConfigurationDisplayContext(
		CookiesConfigurationProvider cookiesConfigurationProvider,
		LayoutUtilityPageEntryLayoutProvider
			layoutUtilityPageEntryLayoutProvider,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		super(
			cookiesConfigurationProvider, layoutUtilityPageEntryLayoutProvider,
			renderRequest, renderResponse);
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

	public String getCookiePolicyLink() throws PortalException {
		String cookiePolicyLink =
			cookiesConsentConfiguration.cookiePolicyLink();

		if (Validator.isNotNull(cookiePolicyLink)) {
			return cookiePolicyLink;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout =
			layoutUtilityPageEntryLayoutProvider.
				getDefaultLayoutUtilityPageEntryLayout(
					themeDisplay.getScopeGroupId(),
					LayoutUtilityPageEntryConstants.TYPE_COOKIE_POLICY);

		if (layout != null) {
			return PortalUtil.getLayoutURL(layout, themeDisplay);
		}

		return StringPool.POUND;
	}

	public String getCookieTitle(
		String cookie, HttpServletRequest httpServletRequest) {

		return LanguageUtil.get(
			httpServletRequest, "cookies-title[" + cookie + "]");
	}

	public String getDescription(Locale locale) {
		LocalizedValuesMap description =
			cookiesConsentConfiguration.description();

		return description.get(locale);
	}

	public String getLinkDisplayText(Locale locale) {
		LocalizedValuesMap linkDisplayTextLocalizedValuesMap =
			cookiesConsentConfiguration.linkDisplayText();

		return linkDisplayTextLocalizedValuesMap.get(locale);
	}

	public boolean isShowButtons() {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return !themeDisplay.isStatePopUp();
	}

}