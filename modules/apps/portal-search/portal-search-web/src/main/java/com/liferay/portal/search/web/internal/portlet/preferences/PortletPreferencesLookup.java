/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.portlet.preferences;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.PortletPreferences;

/**
 * @author André de Oliveira
 */
public interface PortletPreferencesLookup {

	public PortletPreferences fetchPreferences(
		Portlet portlet, ThemeDisplay themeDisplay);

}