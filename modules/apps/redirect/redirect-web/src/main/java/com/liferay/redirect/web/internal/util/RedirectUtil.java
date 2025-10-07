/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.web.internal.util;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;

import java.util.NavigableMap;

/**
 * @author Alejandro Tardín
 */
public class RedirectUtil {

	public static String getGroupBaseURL(ThemeDisplay themeDisplay) {
		StringBuilder groupBaseURL = new StringBuilder();

		groupBaseURL.append(themeDisplay.getPortalURL());

		Group group = themeDisplay.getScopeGroup();

		LayoutSet layoutSet = group.getPublicLayoutSet();

		NavigableMap<String, String> virtualHostnames =
			layoutSet.getVirtualHostnames();

		if (virtualHostnames.isEmpty() ||
			!_matchesHostname(groupBaseURL, virtualHostnames)) {

			groupBaseURL.append(group.getPathFriendlyURL(false, themeDisplay));
			groupBaseURL.append(
				HttpComponentsUtil.decodeURL(group.getFriendlyURL()));
		}

		return groupBaseURL.toString();
	}

	private static boolean _matchesHostname(
		StringBuilder friendlyURLBase,
		NavigableMap<String, String> virtualHostnames) {

		for (String virtualHostname : virtualHostnames.keySet()) {
			if (friendlyURLBase.indexOf(virtualHostname) != -1) {
				return true;
			}
		}

		return false;
	}

}