/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.osb.faro.web.internal.display.context.SettingsNavigationDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseSettingsSectionFragmentRenderer
	extends BaseSectionFragmentRenderer {

	@Override
	protected NavigationItemList getSidebarNavigationItems(
			HttpServletRequest httpServletRequest)
		throws Exception {

		SettingsNavigationDisplayContext settingsNavigationDisplayContext =
			new SettingsNavigationDisplayContext(httpServletRequest);

		return settingsNavigationDisplayContext.getNavigationItems();
	}

}