/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.portal.kernel.theme.ThemeDisplay;

/**
 * @author Christian Dorado
 */
public class ViewHomeRecentAssetsDisplayContext {

	public ViewHomeRecentAssetsDisplayContext(ThemeDisplay themeDisplay) {
		_themeDisplay = themeDisplay;
	}

	private final ThemeDisplay _themeDisplay;

}