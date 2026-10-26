/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.servlet.taglib;

import com.liferay.cookies.banner.web.internal.constants.CookiesBannerWebKeys;
import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.cookies.global.privacy.control.GlobalPrivacyControlProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(service = DynamicInclude.class)
public class CookiesBannerBodyTopJSPDynamicInclude
	extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		if (LiferayWindowState.isPopUp(httpServletRequest)) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		if (group.isStagingGroup()) {
			return;
		}

		boolean previewMode = false;

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (permissionChecker.isCompanyAdmin()) {
			previewMode = ParamUtil.getBoolean(
				httpServletRequest,
				CookiesBannerWebKeys.COOKIES_BANNER_PREVIEW);
		}

		try {
			CookiesPreferenceHandlingConfiguration
				cookiesPreferenceHandlingConfiguration =
					_cookiesConfigurationProvider.
						getCookiesPreferenceHandlingConfiguration(themeDisplay);

			if ((!cookiesPreferenceHandlingConfiguration.active() ||
				 !cookiesPreferenceHandlingConfiguration.enabled()) &&
				!previewMode) {

				return;
			}

			httpServletRequest.setAttribute(
				CookiesBannerWebKeys.COOKIES_BANNER_PREVIEW, previewMode);
			httpServletRequest.setAttribute(
				CookiesBannerWebKeys.CUSTOM_FLOATING_ICON_IMAGE_ID,
				cookiesPreferenceHandlingConfiguration.
					customFloatingIconImageId());
			httpServletRequest.setAttribute(
				CookiesBannerWebKeys.FLOATING_ICON,
				cookiesPreferenceHandlingConfiguration.floatingIcon());
			httpServletRequest.setAttribute(
				CookiesBannerWebKeys.FLOATING_ICON_ENABLED,
				cookiesPreferenceHandlingConfiguration.floatingIconEnabled());
			httpServletRequest.setAttribute(
				CookiesBannerWebKeys.GLOBAL_PRIVACY_CONTROL_SIGNAL_ACTIVE,
				_globalPrivacyControlProvider.isSignalActive(
					httpServletRequest));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/body_top.jsp#post");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/view.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CookiesBannerBodyTopJSPDynamicInclude.class);

	@Reference
	private CookiesConfigurationProvider _cookiesConfigurationProvider;

	@Reference
	private GlobalPrivacyControlProvider _globalPrivacyControlProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.cookies.banner.web)"
	)
	private ServletContext _servletContext;

}