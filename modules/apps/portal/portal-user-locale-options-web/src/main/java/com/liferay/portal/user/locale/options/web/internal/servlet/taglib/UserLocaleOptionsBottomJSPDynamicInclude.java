/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.user.locale.options.web.internal.servlet.taglib;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Chema Balsas
 */
@Component(service = DynamicInclude.class)
public class UserLocaleOptionsBottomJSPDynamicInclude
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

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		if (user.isGuestUser()) {
			return;
		}

		Locale locale = themeDisplay.getLocale();

		if (locale.equals(user.getLocale())) {
			return;
		}

		boolean ignoreUserLocaleOptions = GetterUtil.getBoolean(
			SessionClicks.get(
				httpServletRequest.getSession(),
				"com.liferay.portal.user.locale.options." +
					"web_ignoreUserLocaleOptions",
				Boolean.FALSE.toString()));

		if (ignoreUserLocaleOptions) {
			return;
		}

		boolean showUserLocaleOptionsMessage = ParamUtil.getBoolean(
			httpServletRequest, "showUserLocaleOptionsMessage", true);

		if (!showUserLocaleOptionsMessage) {
			return;
		}

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	public void register(
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry) {

		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#pre");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/user_locale_options.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserLocaleOptionsBottomJSPDynamicInclude.class);

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.user.locale.options.web)"
	)
	private ServletContext _servletContext;

}