/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.portlet.preferences.updater;

import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.PortletPreferences;

/**
 * @author Rafael Praxedes
 */
public interface PortletPreferencesUpdater {

	public void updatePortletPreferences(
			String className, long classPK, String portletId,
			PortletPreferences portletPreferences, ThemeDisplay themeDisplay)
		throws Exception;

}