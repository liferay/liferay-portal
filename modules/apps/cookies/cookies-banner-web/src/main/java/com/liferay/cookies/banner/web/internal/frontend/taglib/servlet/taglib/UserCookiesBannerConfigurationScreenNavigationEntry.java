/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.cookies.banner.web.internal.constants.CookiesBannerScreenNavigationEntryConstants;
import com.liferay.cookies.banner.web.internal.constants.CookiesBannerWebKeys;
import com.liferay.cookies.banner.web.internal.display.context.CookiesBannerConfigurationDisplayContext;
import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christopher Kian
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class UserCookiesBannerConfigurationScreenNavigationEntry
	implements ScreenNavigationEntry<User> {

	@Override
	public String getCategoryKey() {
		return CookiesBannerScreenNavigationEntryConstants.
			CATEGORY_KEY_DATA_AND_PRIVACY;
	}

	@Override
	public String getEntryKey() {
		return CookiesBannerScreenNavigationEntryConstants.
			ENTRY_KEY_COOKIES_BANNER_CONFIGURATION;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(
			_getResourceBundle(locale),
			"cookie-preference-handling-configuration-name");
	}

	@Override
	public String getScreenNavigationKey() {
		return UserScreenNavigationEntryConstants.SCREEN_NAVIGATION_KEY_USERS;
	}

	@Override
	public boolean isVisible(User user, User selUser) {
		if (selUser == null) {
			return false;
		}

		return FeatureFlagManagerUtil.isEnabled(
			user.getCompanyId(), "LPD-51356");
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/cookies_banner_configuration/view.jsp");

		CookiesBannerConfigurationDisplayContext
			cookiesBannerConfigurationDisplayContext =
				new CookiesBannerConfigurationDisplayContext(
					_cookiesConfigurationProvider, httpServletRequest,
					_layoutUtilityPageEntryLayoutProvider);

		httpServletRequest.setAttribute(
			CookiesBannerWebKeys.COOKIES_BANNER_CONFIGURATION_DISPLAY_CONTEXT,
			cookiesBannerConfigurationDisplayContext);

		try {
			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (ServletException servletException) {
			throw new IOException(servletException);
		}
	}

	private ResourceBundle _getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());
	}

	@Reference
	private CookiesConfigurationProvider _cookiesConfigurationProvider;

	@Reference
	private Language _language;

	@Reference
	private LayoutUtilityPageEntryLayoutProvider
		_layoutUtilityPageEntryLayoutProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.cookies.banner.web)"
	)
	private ServletContext _servletContext;

}