/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.launch.web.internal.constants.LaunchScreenNavigationConstants;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "screen.navigation.category.order:Integer=10",
	service = ScreenNavigationCategory.class
)
public class DetailsLaunchScreenNavigationCategory
	extends BaseLaunchScreenNavigationCategory {

	@Override
	public String getCategoryKey() {
		return LaunchScreenNavigationConstants.CATEGORY_KEY_DETAILS;
	}

}