/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.punchout.portal.security.auto.login;

import com.liferay.commerce.punchout.portal.security.auto.login.internal.constants.PunchOutAutoLoginConstants;
import com.liferay.commerce.punchout.portal.security.auto.login.internal.module.configuration.PunchOutAccessTokenAutoLoginConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jaclyn Ong
 */
@Component(
	configurationPid = "com.liferay.commerce.punchout.portal.security.auto.login.internal.module.configuration.PunchOutAccessTokenAutoLoginConfiguration",
	service = AutoLogin.class
)
public class PunchOutAccessTokenAutoLogin extends BaseAutoLogin {

	@Override
	protected String[] doLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (!isEnabled(_portal.getCompanyId(httpServletRequest))) {
			return null;
		}

		return _autoLogin.login(httpServletRequest, httpServletResponse);
	}

	protected boolean isEnabled(long companyId) {
		PunchOutAccessTokenAutoLoginConfiguration
			punchOutAccessTokenAutoLoginConfiguration =
				_getPunchOutAccessTokenAutoLoginConfiguration(companyId);

		if (punchOutAccessTokenAutoLoginConfiguration == null) {
			return false;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Punch out enabled for channel: " +
					punchOutAccessTokenAutoLoginConfiguration.enabled());
		}

		return punchOutAccessTokenAutoLoginConfiguration.enabled();
	}

	private PunchOutAccessTokenAutoLoginConfiguration
		_getPunchOutAccessTokenAutoLoginConfiguration(long companyId) {

		try {
			return _configurationProvider.getConfiguration(
				PunchOutAccessTokenAutoLoginConfiguration.class,
				new CompanyServiceSettingsLocator(
					companyId, PunchOutAutoLoginConstants.SERVICE_NAME));
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to get punch out access token auto login configuration",
				configurationException);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PunchOutAccessTokenAutoLogin.class);

	@Reference(
		target = "(&(private.auto.login=true)(type=punchout.access.token))"
	)
	private AutoLogin _autoLogin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

}