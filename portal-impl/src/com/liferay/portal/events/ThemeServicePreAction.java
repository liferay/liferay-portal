/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ColorSchemeFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.ThemeFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Edward Han
 */
public class ThemeServicePreAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		try {
			servicePre(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}
	}

	protected void servicePre(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Theme theme = themeDisplay.getTheme();

		if (theme != null) {
			if (_log.isInfoEnabled()) {
				_log.info("Theme is already set");
			}

			return;
		}

		ColorScheme colorScheme = themeDisplay.getColorScheme();
		Layout layout = themeDisplay.getLayout();

		if (layout != null) {
			theme = layout.getTheme();
			colorScheme = layout.getColorScheme();
		}
		else {
			String themeId = ThemeFactoryUtil.getDefaultRegularThemeId(
				themeDisplay.getCompanyId());
			String colorSchemeId =
				ColorSchemeFactoryUtil.getDefaultRegularColorSchemeId();

			theme = ThemeLocalServiceUtil.getTheme(
				themeDisplay.getCompanyId(), themeId);

			colorScheme = ThemeLocalServiceUtil.getColorScheme(
				themeDisplay.getCompanyId(), theme.getThemeId(), colorSchemeId);
		}

		httpServletRequest.setAttribute(WebKeys.COLOR_SCHEME, colorScheme);
		httpServletRequest.setAttribute(WebKeys.THEME, theme);

		themeDisplay.setLookAndFeel(theme, colorScheme);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ThemeServicePreAction.class);

}