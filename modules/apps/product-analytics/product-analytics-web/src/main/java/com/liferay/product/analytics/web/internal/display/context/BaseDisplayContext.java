/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.analytics.web.internal.display.context;

import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.ConsentCookieType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.analytics.web.internal.constants.ProductAnalyticsCookiesConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Christopher Kian
 */
public abstract class BaseDisplayContext {

	public BaseDisplayContext(
		HttpServletRequest httpServletRequest,
		LayoutUtilityPageEntryLayoutProvider
			layoutUtilityPageEntryLayoutProvider) {

		_httpServletRequest = httpServletRequest;
		_layoutUtilityPageEntryLayoutProvider =
			layoutUtilityPageEntryLayoutProvider;
	}

	public String getCookieTitle(
		String cookie, HttpServletRequest httpServletRequest) {

		return LanguageUtil.get(
			httpServletRequest, "cookies-title[" + cookie + "]");
	}

	public List<ConsentCookieType> getOptionalConsentCookieTypes() {
		if (_optionalConsentCookieTypes != null) {
			return _optionalConsentCookieTypes;
		}

		_optionalConsentCookieTypes = ListUtil.fromArray(
			_getConsentCookieType(
				false,
				ProductAnalyticsCookiesConstants.
					NAME_PRODUCT_ANALYTICS_CONSENT_TYPE_FUNCTIONAL,
				false),
			_getConsentCookieType(
				false,
				ProductAnalyticsCookiesConstants.
					NAME_PRODUCT_ANALYTICS_CONSENT_TYPE_PERFORMANCE,
				false),
			_getConsentCookieType(
				false,
				ProductAnalyticsCookiesConstants.
					NAME_PRODUCT_ANALYTICS_CONSENT_TYPE_PERSONALIZATION,
				false),
			_getConsentCookieType(
				false,
				ProductAnalyticsCookiesConstants.
					NAME_PRODUCT_ANALYTICS_CONSENT_TYPE_PRODUCT_ANALYTICS,
				false));

		return _optionalConsentCookieTypes;
	}

	public String getPrivacyPolicyLink() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout =
			_layoutUtilityPageEntryLayoutProvider.
				getDefaultLayoutUtilityPageEntryLayout(
					themeDisplay.getScopeGroupId(),
					LayoutUtilityPageEntryConstants.TYPE_COOKIE_POLICY);

		if (layout != null) {
			return PortalUtil.getLayoutURL(layout, themeDisplay);
		}

		return StringPool.POUND;
	}

	public List<ConsentCookieType> getRequiredConsentCookieTypes() {
		if (_requiredConsentCookieTypes != null) {
			return _requiredConsentCookieTypes;
		}

		_requiredConsentCookieTypes = ListUtil.fromArray(
			_getConsentCookieType(
				false,
				ProductAnalyticsCookiesConstants.
					NAME_PRODUCT_ANALYTICS_CONSENT_TYPE_NECESSARY,
				true));

		return _requiredConsentCookieTypes;
	}

	protected JSONArray getConsentCookieTypeNamesJSONArray(
		List<ConsentCookieType> consentCookieTypes) {

		JSONArray consentCookieTypeNamesJSONArray =
			JSONFactoryUtil.createJSONArray();

		for (ConsentCookieType consentCookieType : consentCookieTypes) {
			consentCookieTypeNamesJSONArray.put(consentCookieType.getName());
		}

		return consentCookieTypeNamesJSONArray;
	}

	protected HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	private ConsentCookieType _getConsentCookieType(
		boolean hideFromEndUser, String name, boolean prechecked) {

		return new ConsentCookieType(
			new LocalizedValuesMap(
				LanguageUtil.get(
					_httpServletRequest.getLocale(),
					"cookies-description[" + name + "]")),
			hideFromEndUser, name, prechecked);
	}

	private final HttpServletRequest _httpServletRequest;
	private final LayoutUtilityPageEntryLayoutProvider
		_layoutUtilityPageEntryLayoutProvider;
	private List<ConsentCookieType> _optionalConsentCookieTypes;
	private List<ConsentCookieType> _requiredConsentCookieTypes;

}