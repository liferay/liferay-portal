/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.session.timeout;

import com.liferay.frontend.js.web.internal.session.timeout.configuration.SessionTimeoutConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.session.timeout.SessionTimeout;
import com.liferay.portal.kernel.session.timeout.SessionTimeoutUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	configurationPid = "com.liferay.frontend.js.web.internal.session.timeout.configuration.SessionTimeoutConfiguration",
	service = SessionTimeout.class
)
public class SessionTimeoutImpl implements SessionTimeout {

	@Override
	public int getAutoExtendOffset(HttpServletRequest httpServletRequest) {
		SessionTimeoutConfiguration sessionTimeoutConfiguration =
			_getSessionTimeoutConfiguration(httpServletRequest);

		return sessionTimeoutConfiguration.autoExtendOffset();
	}

	@Override
	public boolean isAutoExtend(HttpServletRequest httpServletRequest) {
		SessionTimeoutConfiguration sessionTimeoutConfiguration =
			_getSessionTimeoutConfiguration(httpServletRequest);

		return sessionTimeoutConfiguration.autoExtend();
	}

	private SessionTimeoutConfiguration _getSessionTimeoutConfiguration(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			return _configurationProvider.getGroupConfiguration(
				SessionTimeoutConfiguration.class,
				themeDisplay.getSiteGroupId());
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get session timeout configuration",
					configurationException);
			}

			return _SESSION_TIMEOUT_CONFIGURATION;
		}
	}

	private static final SessionTimeoutConfiguration
		_SESSION_TIMEOUT_CONFIGURATION = new SessionTimeoutConfiguration() {

			@Override
			public boolean autoExtend() {
				return SessionTimeoutUtil.AUTO_EXTEND;
			}

			@Override
			public int autoExtendOffset() {
				return SessionTimeoutUtil.AUTO_EXTEND_OFFSET;
			}

		};

	private static final Log _log = LogFactoryUtil.getLog(
		SessionTimeoutImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

}