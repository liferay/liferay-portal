/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.consent.management.platform.integration.internal.consent;

import com.liferay.consent.management.platform.integration.configuration.ConsentManagementPlatformConfiguration;
import com.liferay.cookies.consent.CookiesConsentResolver;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christian Moura
 */
@Component(
	property = "service.ranking:Integer=100",
	service = CookiesConsentResolver.class
)
public class ThirdPartyCookiesConsentResolver
	implements CookiesConsentResolver {

	@Override
	public boolean hasConsent(
		int consentType, HttpServletRequest httpServletRequest) {

		if (httpServletRequest == null) {
			return _defaultCookiesConsentResolver.hasConsent(
				consentType, httpServletRequest);
		}

		long companyId = _portal.getCompanyId(httpServletRequest);

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-65299")) {
			return _defaultCookiesConsentResolver.hasConsent(
				consentType, httpServletRequest);
		}

		if (consentType == CookiesConstants.CONSENT_TYPE_NECESSARY) {
			return true;
		}

		boolean thirdPartyEnabled = false;

		try {
			ConsentManagementPlatformConfiguration
				consentManagementPlatformConfiguration = null;

			long groupId = _portal.getScopeGroupId(httpServletRequest);

			if (groupId > 0) {
				consentManagementPlatformConfiguration =
					_configurationProvider.getGroupConfiguration(
						ConsentManagementPlatformConfiguration.class, companyId,
						groupId);
			}
			else {
				consentManagementPlatformConfiguration =
					_configurationProvider.getCompanyConfiguration(
						ConsentManagementPlatformConfiguration.class,
						companyId);
			}

			if ((consentManagementPlatformConfiguration != null) &&
				consentManagementPlatformConfiguration.enabled()) {

				thirdPartyEnabled = true;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to read consent management platform configuration",
					exception);
			}
		}

		if (!thirdPartyEnabled) {
			return _defaultCookiesConsentResolver.hasConsent(
				consentType, httpServletRequest);
		}

		String cookieValue = CookiesManagerUtil.getCookieValue(
			CookiesConstants.NAME_LIFERAY_CONSENT_STATE, httpServletRequest);

		if (Validator.isNull(cookieValue)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					CookiesConstants.NAME_LIFERAY_CONSENT_STATE +
						" cookie is absent");
			}

			return true;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				CookiesConstants.NAME_LIFERAY_CONSENT_STATE +
					" cookie value: " + cookieValue);
		}

		String key = null;

		if (consentType == CookiesConstants.CONSENT_TYPE_FUNCTIONAL) {
			key = "functional";
		}
		else if (consentType == CookiesConstants.CONSENT_TYPE_PERFORMANCE) {
			key = "performance";
		}
		else if (consentType == CookiesConstants.CONSENT_TYPE_PERSONALIZATION) {
			key = "personalization";
		}

		if (key == null) {
			return _defaultCookiesConsentResolver.hasConsent(
				consentType, httpServletRequest);
		}

		try {
			JSONObject cookieValueJSONObject = _jsonFactory.createJSONObject(
				URLDecoder.decode(cookieValue, StandardCharsets.UTF_8));

			if (!cookieValueJSONObject.has(key)) {
				return true;
			}

			return cookieValueJSONObject.getBoolean(key);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to parse ",
						CookiesConstants.NAME_LIFERAY_CONSENT_STATE,
						" cookie value: ", exception.getMessage()));
			}

			return true;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ThirdPartyCookiesConsentResolver.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(
		target = "(component.name=com.liferay.cookies.internal.consent.DefaultCookiesConsentResolver)"
	)
	private CookiesConsentResolver _defaultCookiesConsentResolver;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}