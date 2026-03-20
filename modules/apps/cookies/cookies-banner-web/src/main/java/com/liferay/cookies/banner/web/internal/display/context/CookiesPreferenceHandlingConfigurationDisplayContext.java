/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.display.context;

import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Rachael Koestartyo
 */
public class CookiesPreferenceHandlingConfigurationDisplayContext {

	public CookiesPreferenceHandlingConfigurationDisplayContext(
		CookiesConfigurationProvider cookiesConfigurationProvider,
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		_cookiesConfigurationProvider = cookiesConfigurationProvider;
		_scope = scope;
		_scopePK = scopePK;
	}

	public int getCookiesPreferenceHandlingConsentRenewalPeriod() {
		return _cookiesConfigurationProvider.
			getCookiesPreferenceHandlingConsentRenewalPeriod(_scope, _scopePK);
	}

	public long getCookiesPreferenceHandlingCustomFloatingIconImageId() {
		return _cookiesConfigurationProvider.
			getCookiesPreferenceHandlingCustomFloatingIconImageId(
				_scope, _scopePK);
	}

	public int getCookiesPreferenceHandlingDissentRenewalPeriod() {
		return _cookiesConfigurationProvider.
			getCookiesPreferenceHandlingDissentRenewalPeriod(_scope, _scopePK);
	}

	public boolean getCookiesPreferenceHandlingEnabled() {
		return _cookiesConfigurationProvider.isCookiesPreferenceHandlingEnabled(
			_scope, _scopePK);
	}

	public boolean getCookiesPreferenceHandlingExplicitConsentMode() {
		return _cookiesConfigurationProvider.
			isCookiesPreferenceHandlingExplicitConsentMode(_scope, _scopePK);
	}

	public String getCookiesPreferenceHandlingFloatingIcon() {
		return _cookiesConfigurationProvider.
			getCookiesPreferenceHandlingFloatingIcon(_scope, _scopePK);
	}

	public boolean getCookiesPreferenceHandlingFloatingIconEnabled() {
		return _cookiesConfigurationProvider.
			isCookiesPreferenceHandlingFloatingIconEnabled(_scope, _scopePK);
	}

	public boolean getCookiesPreferenceHandlingStoreConsent() {
		return _cookiesConfigurationProvider.
			isCookiesPreferenceHandlingStoreConsent(_scope, _scopePK);
	}

	private final CookiesConfigurationProvider _cookiesConfigurationProvider;
	private final ExtendedObjectClassDefinition.Scope _scope;
	private final long _scopePK;

}