/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.NavigableMap;

/**
 * @author David Truong
 * @author Jesse Rao
 */
public class RobotsUtil {

	public static String getRobots(LayoutSet layoutSet, boolean secure)
		throws PortalException {

		if (layoutSet == null) {
			try {
				return com.liferay.petra.string.StringUtil.read(
					RobotsUtil.class.getClassLoader(),
					PropsValues.ROBOTS_TXT_WITHOUT_SITEMAP);
			}
			catch (IOException ioException) {
				_log.error(
					"Unable to read the content for " +
						PropsValues.ROBOTS_TXT_WITHOUT_SITEMAP,
					ioException);
			}
		}

		int portalServerPort = PortalUtil.getPortalServerPort(secure);

		NavigableMap<String, String> virtualHostnames =
			PortalUtil.getVirtualHostnames(layoutSet);

		String virtualHostname = StringPool.BLANK;

		if (!virtualHostnames.isEmpty()) {
			virtualHostname = virtualHostnames.firstKey();
		}

		String robotsTxt = null;

		try {
			robotsTxt = GetterUtil.getString(
				layoutSet.getSettingsProperty(
					layoutSet.isPrivateLayout() + "-robots.txt"),
				StringUtil.read(
					RobotsUtil.class.getClassLoader(),
					PropsValues.ROBOTS_TXT_WITH_SITEMAP));
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to read the content for " +
					PropsValues.ROBOTS_TXT_WITH_SITEMAP,
				ioException);
		}

		return _replaceWildcards(
			robotsTxt, virtualHostname, secure, portalServerPort);
	}

	private static String _replaceWildcards(
		String robotsTxt, String virtualHostname, boolean secure, int port) {

		if (Validator.isNotNull(virtualHostname)) {
			robotsTxt = StringUtil.replace(
				robotsTxt, "[$HOST$]", virtualHostname);
		}
		else if (_log.isWarnEnabled()) {
			_log.warn(
				"Placeholder [$HOST$] could not be replaced with the actual " +
					"host");
		}

		robotsTxt = StringUtil.replace(
			robotsTxt, "[$PORT$]", String.valueOf(port));

		if (secure) {
			return StringUtil.replace(robotsTxt, "[$PROTOCOL$]", "https");
		}

		return StringUtil.replace(robotsTxt, "[$PROTOCOL$]", "http");
	}

	private static final Log _log = LogFactoryUtil.getLog(RobotsUtil.class);

}