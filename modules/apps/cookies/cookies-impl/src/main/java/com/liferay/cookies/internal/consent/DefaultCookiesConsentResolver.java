/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.consent;

import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.cookies.consent.CookiesConsentResolver;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christian Moura
 */
@Component(service = CookiesConsentResolver.class)
public class DefaultCookiesConsentResolver implements CookiesConsentResolver {

	@Override
	public boolean hasConsent(
		int consentType, HttpServletRequest httpServletRequest) {

		if (consentType == CookiesConstants.CONSENT_TYPE_NECESSARY) {
			return true;
		}

		String consentCookieName = StringPool.BLANK;

		if (consentType == CookiesConstants.CONSENT_TYPE_FUNCTIONAL) {
			consentCookieName = CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL;
		}
		else if (consentType == CookiesConstants.CONSENT_TYPE_PERFORMANCE) {
			consentCookieName = CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE;
		}
		else if (consentType == CookiesConstants.CONSENT_TYPE_PERSONALIZATION) {
			consentCookieName =
				CookiesConstants.NAME_CONSENT_TYPE_PERSONALIZATION;
		}

		String consentCookieValue = CookiesManagerUtil.getCookieValue(
			consentCookieName, httpServletRequest);

		if (Validator.isNotNull(consentCookieValue)) {
			return GetterUtil.getBoolean(consentCookieValue);
		}

		try {
			CookiesPreferenceHandlingConfiguration
				cookiesPreferenceHandlingConfiguration = null;

			if (httpServletRequest != null) {
				long groupId = _portal.getScopeGroupId(httpServletRequest);

				if (groupId > 0) {
					cookiesPreferenceHandlingConfiguration =
						_configurationProvider.getGroupConfiguration(
							CookiesPreferenceHandlingConfiguration.class,
							_portal.getCompanyId(httpServletRequest), groupId);
				}
				else {
					cookiesPreferenceHandlingConfiguration =
						_configurationProvider.getCompanyConfiguration(
							CookiesPreferenceHandlingConfiguration.class,
							_portal.getCompanyId(httpServletRequest));
				}
			}
			else {
				cookiesPreferenceHandlingConfiguration =
					_configurationProvider.getSystemConfiguration(
						CookiesPreferenceHandlingConfiguration.class);
			}

			return !cookiesPreferenceHandlingConfiguration.
				explicitConsentMode();
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

}