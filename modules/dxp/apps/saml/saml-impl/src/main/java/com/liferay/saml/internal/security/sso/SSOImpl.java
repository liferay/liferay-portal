/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.internal.security.sso;

import com.liferay.portal.kernel.security.sso.SSO;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(service = SSO.class)
public class SSOImpl implements SSO {

	@Override
	public String getSessionExpirationRedirectUrl(long companyId) {
		return null;
	}

	@Override
	public String getSignInURL(long companyId, String defaultSigninURL) {
		if (_samlProviderConfigurationHelper.isEnabled()) {
			return defaultSigninURL;
		}

		return null;
	}

	@Override
	public boolean isLoginRedirectRequired(long companyId) {
		return _samlProviderConfigurationHelper.isEnabled();
	}

	@Override
	public boolean isRedirectRequired(long companyId) {
		return false;
	}

	@Override
	public boolean isSessionRedirectOnExpire(long companyId) {
		return false;
	}

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

}