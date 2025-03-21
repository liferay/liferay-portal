/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.theme;

import com.liferay.portal.kernel.servlet.ServletContextPool;

import jakarta.servlet.ServletContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class ThemeLoaderFactory {

	public static boolean destroy(String servletContextName) {
		ThemeLoader themeLoader = _themeLoaders.remove(servletContextName);

		if (themeLoader == null) {
			return false;
		}

		ServletContextPool.remove(servletContextName);

		themeLoader.destroy();

		return true;
	}

	public static ThemeLoader getDefaultThemeLoader() {
		ThemeLoader themeLoader = null;

		for (Map.Entry<String, ThemeLoader> entry : _themeLoaders.entrySet()) {
			themeLoader = entry.getValue();

			break;
		}

		return themeLoader;
	}

	public static ThemeLoader getThemeLoader(String servletContextName) {
		return _themeLoaders.get(servletContextName);
	}

	public static void init(
		String servletContextName, ServletContext servletContext,
		String[] xmls) {

		ServletContextPool.put(servletContextName, servletContext);

		ThemeLoader themeLoader = new ThemeLoader(
			servletContextName, servletContext, xmls);

		_themeLoaders.put(servletContextName, themeLoader);
	}

	public static void loadThemes() {
		for (Map.Entry<String, ThemeLoader> entry : _themeLoaders.entrySet()) {
			ThemeLoader themeLoader = entry.getValue();

			themeLoader.loadThemes();
		}
	}

	private static final Map<String, ThemeLoader> _themeLoaders =
		new HashMap<>();

}