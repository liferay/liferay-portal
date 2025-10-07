/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Map;

/**
 * @author Christian Dorado
 */
public class ViewHomeSearchBarDisplayContext {

	public ViewHomeSearchBarDisplayContext(ThemeDisplay themeDisplay) {
		_themeDisplay = themeDisplay;
	}

	public Map<String, Object> getProps() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"searchResultsURL",
			PortalUtil.getLayoutFullURL(
				LayoutLocalServiceUtil.getLayoutByFriendlyURL(
					_themeDisplay.getScopeGroupId(), false, "/all"),
				_themeDisplay)
		).put(
			"userFirstName",
			() -> {
				User user = _themeDisplay.getUser();

				return user.getFirstName();
			}
		).build();
	}

	private final ThemeDisplay _themeDisplay;

}