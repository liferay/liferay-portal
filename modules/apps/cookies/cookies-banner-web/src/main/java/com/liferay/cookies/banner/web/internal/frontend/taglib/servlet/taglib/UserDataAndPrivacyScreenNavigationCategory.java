/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.cookies.banner.web.internal.constants.CookiesBannerScreenNavigationEntryConstants;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christopher Kian
 */
@Component(
	property = "screen.navigation.category.order:Integer=40",
	service = ScreenNavigationCategory.class
)
public class UserDataAndPrivacyScreenNavigationCategory
	implements ScreenNavigationCategory {

	@Override
	public String getCategoryKey() {
		return CookiesBannerScreenNavigationEntryConstants.
			CATEGORY_KEY_DATA_AND_PRIVACY;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "data-and-privacy");
	}

	@Override
	public String getScreenNavigationKey() {
		return UserScreenNavigationEntryConstants.SCREEN_NAVIGATION_KEY_USERS;
	}

	@Reference
	private Language _language;

}