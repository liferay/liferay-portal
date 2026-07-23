/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.consent;

import com.liferay.consent.management.platform.integration.configuration.ConsentManagementPlatformConfiguration;
import com.liferay.cookies.consent.CookiesConsentChecker;
import com.liferay.cookies.consent.CookiesConsentCheckerResolver;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christian Moura
 */
@Component(service = CookiesConsentCheckerResolver.class)
public class CookiesConsentCheckerResolverImpl
	implements CookiesConsentCheckerResolver {

	@Override
	public CookiesConsentChecker getCookiesConsentChecker(
		HttpServletRequest httpServletRequest) {

		if (httpServletRequest == null) {
			return _defaultCookiesConsentChecker;
		}

		try {
			long companyId = _portal.getCompanyId(httpServletRequest);
			long groupId = _portal.getScopeGroupId(httpServletRequest);

			ConsentManagementPlatformConfiguration
				consentManagementPlatformConfiguration = null;

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

				CookiesConsentChecker cookiesConsentChecker =
					_cookiesConsentCheckerSnapshot.get();

				if (cookiesConsentChecker != null) {
					return cookiesConsentChecker;
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to read consent management platform configuration",
					exception);
			}
		}

		return _defaultCookiesConsentChecker;
	}

	@Activate
	protected void activate() {
		_defaultCookiesConsentChecker = new DefaultCookiesConsentChecker(
			_configurationProvider, _portal);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CookiesConsentCheckerResolverImpl.class);

	private static final Snapshot<CookiesConsentChecker>
		_cookiesConsentCheckerSnapshot = new Snapshot<>(
			CookiesConsentCheckerResolverImpl.class,
			CookiesConsentChecker.class, null, true);

	@Reference
	private ConfigurationProvider _configurationProvider;

	private CookiesConsentChecker _defaultCookiesConsentChecker;

	@Reference
	private Portal _portal;

}