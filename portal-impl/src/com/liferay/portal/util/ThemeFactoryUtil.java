/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.model.impl.ThemeImpl;

/**
 * @author Harrison Schueler
 */
public class ThemeFactoryUtil {

	public static Theme getDefaultRegularTheme(long companyId) {
		return new ThemeImpl(
			getDefaultRegularThemeId(companyId), StringPool.BLANK);
	}

	public static String getDefaultRegularThemeId(long companyId) {
		String defaultRegularThemeId = PrefsPropsUtil.getString(
			companyId, PropsKeys.DEFAULT_REGULAR_THEME_ID);

		return PortalUtil.getJsSafePortletId(defaultRegularThemeId);
	}

	public static Theme getTheme() {
		return new ThemeImpl();
	}

	public static Theme getTheme(String themeId) {
		return new ThemeImpl(themeId);
	}

	public static Theme getTheme(String themeId, String name) {
		return new ThemeImpl(themeId, name);
	}

}