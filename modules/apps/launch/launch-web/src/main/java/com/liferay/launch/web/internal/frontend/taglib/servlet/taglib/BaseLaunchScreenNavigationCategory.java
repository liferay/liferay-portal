/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.launch.web.internal.constants.LaunchScreenNavigationConstants;
import com.liferay.portal.kernel.language.Language;

import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
public abstract class BaseLaunchScreenNavigationCategory
	implements ScreenNavigationCategory {

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, getCategoryKey());
	}

	@Override
	public String getScreenNavigationKey() {
		return LaunchScreenNavigationConstants.SCREEN_NAVIGATION_KEY_LAUNCH;
	}

	@Reference
	protected Language language;

}