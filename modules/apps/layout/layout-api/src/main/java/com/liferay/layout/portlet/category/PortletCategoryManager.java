/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.portlet.category;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public interface PortletCategoryManager {

	public JSONArray getPortletsJSONArray(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws Exception;

	public void updateSortedPortletCategoryKeys(
		PortalPreferences portalPreferences,
		String[] sortedPortletCategoryKeys);

}