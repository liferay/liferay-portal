/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.content.internal.events;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ColorSchemeFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class HistoryLayoutModeThemePreAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		String layoutMode = ParamUtil.getString(httpServletRequest, "p_l_mode");

		if (!Constants.HISTORY.equals(layoutMode)) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return;
		}

		Layout layout = themeDisplay.getLayout();

		if ((layout == null) || !layout.isDraftLayout() ||
			!layout.isTypeContent()) {

			return;
		}

		try {
			String themeId = PrefsPropsUtil.getString(
				themeDisplay.getCompanyId(),
				PropsKeys.CONTROL_PANEL_LAYOUT_REGULAR_THEME_ID);

			Theme theme = _themeLocalService.getTheme(
				themeDisplay.getCompanyId(), themeId);

			ColorScheme colorScheme = _themeLocalService.getColorScheme(
				themeDisplay.getCompanyId(), themeId,
				ColorSchemeFactoryUtil.getDefaultRegularColorSchemeId());

			httpServletRequest.setAttribute(WebKeys.COLOR_SCHEME, colorScheme);

			httpServletRequest.setAttribute(WebKeys.THEME, theme);

			themeDisplay.setLookAndFeel(theme, colorScheme);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to apply the control panel theme to the history " +
					"layout mode",
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HistoryLayoutModeThemePreAction.class);

	@Reference
	private ThemeLocalService _themeLocalService;

}