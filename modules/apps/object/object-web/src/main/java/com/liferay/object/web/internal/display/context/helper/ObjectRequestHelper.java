/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.display.context.helper;

import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Marco Leo
 */
public class ObjectRequestHelper extends BaseRequestHelper {

	public ObjectRequestHelper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	public Locale getDefaultLocale() {
		ThemeDisplay themeDisplay = getThemeDisplay();

		Locale locale = themeDisplay.getSiteDefaultLocale();

		if (locale == null) {
			return super.getLocale();
		}

		return locale;
	}

}