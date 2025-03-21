/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auto.login.basic.auth.header;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.auto.login.internal.basic.auth.header.configuration.BasicAuthHeaderAutoLoginConfiguration;
import com.liferay.portal.security.auto.login.internal.basic.auth.header.constants.BasicAuthHeaderAutoLoginConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * <p>
 * 1. Install Firefox. These instructions assume you have Firefox 2.0.0.1.
 * Previous version of Firefox have been tested and are known to work.
 * </p>
 *
 * <p>
 * 2. Install the Modify Headers 0.5.4 Add-on. Tools > Add Ons. Click the get
 * extensions link at the bottom of the window. Type in "Modify Headers" in the
 * Search box. Find Modify Headers in the results page and click on it. Then
 * click the install now link.
 * </p>
 *
 * <p>
 * 3. Configure Modify Headers to add a basic authentication header. Tools >
 * Modify Headers. In the Modify Headers window select the Add drop down. Type
 * in "Authorization" in the next box. Type in "Basic bGlmZXJheS5jb20uMTp0ZXN0"
 * in the next box. Click the Add button.
 * </p>
 *
 * <p>
 * 4. Make sure your header modification is enabled and point your browser to
 * the Liferay portal.
 * </p>
 *
 * <p>
 * 5. You should now be authenticated as Joe Bloggs.
 * </p>
 *
 * @author Britt Courtney
 * @author Brian Wing Shun Chan
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.portal.security.auto.login.internal.basic.auth.header.configuration.BasicAuthHeaderAutoLoginConfiguration",
	property = "type=basic.auth.header", service = AutoLogin.class
)
public class BasicAuthHeaderAutoLogin extends BaseAutoLogin {

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
		BasicAuthHeaderAutoLoginConfiguration
			basicAuthHeaderAutoLoginConfiguration =
				_getBasicAuthHeaderAutoLoginConfiguration(companyId);

		if (basicAuthHeaderAutoLoginConfiguration == null) {
			return false;
		}

		return basicAuthHeaderAutoLoginConfiguration.enabled();
	}

	private BasicAuthHeaderAutoLoginConfiguration
		_getBasicAuthHeaderAutoLoginConfiguration(long companyId) {

		try {
			return _configurationProvider.getConfiguration(
				BasicAuthHeaderAutoLoginConfiguration.class,
				new CompanyServiceSettingsLocator(
					companyId, BasicAuthHeaderAutoLoginConstants.SERVICE_NAME,
					BasicAuthHeaderAutoLoginConfiguration.class.getName()));
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to get basic auth header configuration",
				configurationException);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BasicAuthHeaderAutoLogin.class);

	@Reference(target = "(&(private.auto.login=true)(type=basic.auth.header))")
	private AutoLogin _autoLogin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

}