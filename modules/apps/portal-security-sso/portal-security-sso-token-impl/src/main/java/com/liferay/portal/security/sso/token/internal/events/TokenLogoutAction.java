/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.token.internal.events;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.token.configuration.TokenConfiguration;
import com.liferay.portal.security.sso.token.constants.TokenConstants;
import com.liferay.portal.security.sso.token.events.LogoutProcessor;
import com.liferay.portal.security.sso.token.events.LogoutProcessorType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * Participates in the user logout process.
 *
 * <p>
 * <code>TokenLogoutAction</code> carries out two tasks:
 * </p>
 *
 * <ol>
 * <li>
 * If authentication cookies are configured, all named cookies are deleted by
 * the <code>@Component</code> defined in the class
 * {@link CookieLogoutProcessor} (which implements {@link LogoutProcessor})
 * </li>
 * <li>
 * If a logout redirect URL is set, then an HTTP redirect response to the
 * specified URL is issued by the <code>@Component</code> defined in the class
 * {@link RedirectLogoutProcessor} (which implements {@link
 * com.liferay.portal.security.sso.token.auto.events.LogoutProcessor})
 * </li>
 * </ol>
 *
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.security.sso.token.configuration.TokenConfiguration",
	property = "key=logout.events.post", service = LifecycleAction.class
)
public class TokenLogoutAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			TokenConfiguration tokenConfiguration =
				_configurationProvider.getConfiguration(
					TokenConfiguration.class,
					new CompanyServiceSettingsLocator(
						_portal.getCompanyId(httpServletRequest),
						TokenConstants.SERVICE_NAME));

			if (!tokenConfiguration.enabled()) {
				return;
			}

			String[] authenticationCookies =
				tokenConfiguration.authenticationCookies();

			if (ArrayUtil.isNotEmpty(authenticationCookies)) {
				LogoutProcessor cookieLogoutProcessor =
					_serviceTrackerMap.getService(LogoutProcessorType.COOKIE);

				if (cookieLogoutProcessor != null) {
					cookieLogoutProcessor.logout(
						httpServletRequest, httpServletResponse,
						authenticationCookies);
				}
			}

			String logoutRedirectURL = tokenConfiguration.logoutRedirectURL();

			if (Validator.isNotNull(logoutRedirectURL)) {
				LogoutProcessor redirectLogoutProcessor =
					_serviceTrackerMap.getService(LogoutProcessorType.REDIRECT);

				if (redirectLogoutProcessor != null) {
					redirectLogoutProcessor.logout(
						httpServletRequest, httpServletResponse,
						logoutRedirectURL);
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, LogoutProcessor.class, "logout.processor.type");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TokenLogoutAction.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, LogoutProcessor> _serviceTrackerMap;

}