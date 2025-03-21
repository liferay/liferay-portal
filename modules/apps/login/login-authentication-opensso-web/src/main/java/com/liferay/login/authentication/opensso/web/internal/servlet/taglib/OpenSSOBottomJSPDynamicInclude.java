/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.authentication.opensso.web.internal.servlet.taglib;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.sso.opensso.configuration.OpenSSOConfiguration;
import com.liferay.portal.security.sso.opensso.constants.OpenSSOConstants;
import com.liferay.portal.security.sso.opensso.constants.OpenSSOWebKeys;
import com.liferay.portal.security.sso.opensso.exception.StrangersNotAllowedException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(service = DynamicInclude.class)
public class OpenSSOBottomJSPDynamicInclude extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-36719")) {

			return;
		}

		try {
			OpenSSOConfiguration openSSOConfiguration =
				_configurationProvider.getConfiguration(
					OpenSSOConfiguration.class,
					new CompanyServiceSettingsLocator(
						_portal.getCompanyId(httpServletRequest),
						OpenSSOConstants.SERVICE_NAME));

			if (!openSSOConfiguration.enabled()) {
				return;
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			return;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String error = (String)originalHttpServletRequest.getAttribute(
			OpenSSOWebKeys.OPEN_SSO_ERROR);

		if (Validator.isBlank(error)) {
			return;
		}

		originalHttpServletRequest.removeAttribute(
			OpenSSOWebKeys.OPEN_SSO_ERROR);

		if (ArrayUtil.contains(_ERRORS, error)) {
			SessionMessages.add(httpServletRequest, error);
		}

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	public void register(
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry) {

		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/com.liferay.portal/opensso_error.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final String[] _ERRORS = {
		ContactNameException.class.getSimpleName(),
		PrincipalException.MustBeAuthenticated.class.getSimpleName(),
		StrangersNotAllowedException.class.getSimpleName(),
		UserEmailAddressException.MustNotUseCompanyMx.class.getSimpleName()
	};

	private static final Log _log = LogFactoryUtil.getLog(
		OpenSSOBottomJSPDynamicInclude.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.login.authentication.opensso.web)"
	)
	private ServletContext _servletContext;

}