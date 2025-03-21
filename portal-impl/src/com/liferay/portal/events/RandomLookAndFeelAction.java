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
import com.liferay.portal.kernel.security.RandomUtil;
import com.liferay.portal.kernel.service.LayoutServiceUtil;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class RandomLookAndFeelAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		try {

			// Do not randomize look and feel unless the user is logged in

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (!themeDisplay.isSignedIn()) {
				return;
			}

			// Do not randomize look and feel unless the user is accessing the
			// portal

			String requestURI = GetterUtil.getString(
				httpServletRequest.getRequestURI());

			if (!requestURI.endsWith("/portal/layout")) {
				return;
			}

			// Do not randomize look and feel unless the user is accessing a
			// personal layout

			Layout layout = themeDisplay.getLayout();

			if (layout == null) {
				return;
			}

			List<Theme> themes = ThemeLocalServiceUtil.getPageThemes(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
				themeDisplay.getUserId());

			if (!themes.isEmpty()) {
				Theme theme = themes.get(RandomUtil.nextInt(themes.size()));

				List<ColorScheme> colorSchemes = theme.getColorSchemes();

				ColorScheme colorScheme = colorSchemes.get(
					RandomUtil.nextInt(colorSchemes.size()));

				LayoutServiceUtil.updateLookAndFeel(
					layout.getGroupId(), layout.isPrivateLayout(),
					layout.getPlid(), theme.getThemeId(),
					colorScheme.getColorSchemeId(), layout.getCss());

				themeDisplay.setLookAndFeel(theme, colorScheme);

				httpServletRequest.setAttribute(
					WebKeys.COLOR_SCHEME, colorScheme);

				httpServletRequest.setAttribute(WebKeys.THEME, theme);
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new ActionException(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RandomLookAndFeelAction.class);

}