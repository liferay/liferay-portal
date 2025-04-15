/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.display.context;

import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.cookies.configuration.banner.CookiesBannerConfiguration;
import com.liferay.cookies.configuration.consent.CookiesConsentConfiguration;
import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.portal.kernel.cookies.ConsentCookieType;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class BaseCookiesBannerDisplayContext {

	public BaseCookiesBannerDisplayContext(
		CookiesConfigurationProvider cookiesConfigurationProvider,
		LayoutUtilityPageEntryLayoutProvider
			layoutUtilityPageEntryLayoutProvider,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_cookiesConfigurationProvider = cookiesConfigurationProvider;
		this.layoutUtilityPageEntryLayoutProvider =
			layoutUtilityPageEntryLayoutProvider;
		this.renderRequest = renderRequest;
		this.renderResponse = renderResponse;

		cookiesBannerConfiguration = _getCookiesBannerConfiguration(
			renderRequest);
		cookiesConsentConfiguration = _getCookiesConsentConfiguration(
			renderRequest);
	}

	public List<ConsentCookieType> getOptionalConsentCookieTypes() {
		if (_optionalConsentCookieTypes != null) {
			return _optionalConsentCookieTypes;
		}

		_optionalConsentCookieTypes = ListUtil.fromArray(
			new ConsentCookieType(
				cookiesConsentConfiguration.functionalCookiesDescription(),
				CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL,
				cookiesConsentConfiguration.functionalCookiesPrechecked()),
			new ConsentCookieType(
				cookiesConsentConfiguration.performanceCookiesDescription(),
				CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE,
				cookiesConsentConfiguration.performanceCookiesPrechecked()),
			new ConsentCookieType(
				cookiesConsentConfiguration.personalizationCookiesDescription(),
				CookiesConstants.NAME_CONSENT_TYPE_PERSONALIZATION,
				cookiesConsentConfiguration.
					personalizationCookiesPrechecked()));

		return _optionalConsentCookieTypes;
	}

	public List<ConsentCookieType> getRequiredConsentCookieTypes() {
		if (_requiredConsentCookieTypes != null) {
			return _requiredConsentCookieTypes;
		}

		_requiredConsentCookieTypes = ListUtil.fromArray(
			new ConsentCookieType(
				cookiesConsentConfiguration.
					strictlyNecessaryCookiesDescription(),
				CookiesConstants.NAME_CONSENT_TYPE_NECESSARY, true));

		return _requiredConsentCookieTypes;
	}

	public boolean isIncludeDeclineAllButton() {
		return cookiesBannerConfiguration.includeDeclineAllButton();
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

	protected CookiesBannerConfiguration cookiesBannerConfiguration;
	protected CookiesConsentConfiguration cookiesConsentConfiguration;
	protected LayoutUtilityPageEntryLayoutProvider
		layoutUtilityPageEntryLayoutProvider;
	protected RenderRequest renderRequest;
	protected RenderResponse renderResponse;

	private CookiesBannerConfiguration _getCookiesBannerConfiguration(
		RenderRequest renderRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			return _cookiesConfigurationProvider.getCookiesBannerConfiguration(
				themeDisplay);
		}
		catch (Exception exception) {
			_log.error("Unable to get cookies banner configuration", exception);
		}

		return null;
	}

	private CookiesConsentConfiguration _getCookiesConsentConfiguration(
		RenderRequest renderRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			return _cookiesConfigurationProvider.getCookiesConsentConfiguration(
				themeDisplay);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get cookies consent configuration", exception);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseCookiesBannerDisplayContext.class);

	private final CookiesConfigurationProvider _cookiesConfigurationProvider;
	private List<ConsentCookieType> _optionalConsentCookieTypes;
	private List<ConsentCookieType> _requiredConsentCookieTypes;

}