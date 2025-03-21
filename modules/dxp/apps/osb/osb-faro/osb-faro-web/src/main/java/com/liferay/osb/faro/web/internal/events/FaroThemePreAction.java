/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.events;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leilany Ulisses
 * @author Marcos Martins
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class FaroThemePreAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (StringUtil.contains(
					httpServletRequest.getRequestURI(), "/c/portal/login") &&
				themeDisplay.isSignedIn()) {

				httpServletResponse.sendRedirect("/");

				return;
			}

			Layout layout = themeDisplay.getLayout();

			if (layout.isTypeControlPanel()) {
				return;
			}

			Theme theme = _themeLocalService.getTheme(
				themeDisplay.getCompanyId(), "osbfarotheme_WAR_osbfarotheme");

			httpServletRequest.setAttribute(WebKeys.THEME, theme);

			themeDisplay.setLookAndFeel(theme, themeDisplay.getColorScheme());
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroThemePreAction.class);

	@Reference
	private ThemeLocalService _themeLocalService;

}