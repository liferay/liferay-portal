/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Iván Zaera Avellón
 */
public class SearchDisplayStyleUtil {

	public static String getDisplayStyle(
		HttpServletRequest httpServletRequest, String portletName,
		String defaultValue) {

		return getDisplayStyle(
			httpServletRequest, portletName, "display-style", defaultValue);
	}

	public static String getDisplayStyle(
		HttpServletRequest httpServletRequest, String portletName,
		String defaultValue, boolean clearCache) {

		return getDisplayStyle(
			httpServletRequest, portletName, "display-style", defaultValue,
			clearCache);
	}

	public static String getDisplayStyle(
		HttpServletRequest httpServletRequest, String portletName, String key,
		String defaultValue) {

		return getDisplayStyle(
			httpServletRequest, portletName, key, defaultValue, false);
	}

	public static String getDisplayStyle(
		HttpServletRequest httpServletRequest, String portletName, String key,
		String defaultValue, boolean clearCache) {

		String displayStyle = ParamUtil.getString(
			httpServletRequest, "displayStyle");

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				httpServletRequest);

		if (Validator.isNull(displayStyle)) {
			displayStyle = portalPreferences.getValue(
				portletName, key, defaultValue);
		}
		else {
			portalPreferences.setValue(portletName, key, displayStyle);

			if (clearCache) {
				httpServletRequest.setAttribute(
					WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);
			}
		}

		return displayStyle;
	}

	public static String getDisplayStyle(
		PortletRequest portletRequest, String portletName,
		String defaultValue) {

		return getDisplayStyle(
			PortalUtil.getHttpServletRequest(portletRequest), portletName,
			defaultValue);
	}

	public static String getDisplayStyle(
		PortletRequest portletRequest, String portletName, String key,
		String defaultValue) {

		return getDisplayStyle(
			PortalUtil.getHttpServletRequest(portletRequest), portletName, key,
			defaultValue);
	}

	public static String getDisplayStyle(
		PortletRequest portletRequest, String portletName, String key,
		String defaultValue, boolean clearCache) {

		return getDisplayStyle(
			PortalUtil.getHttpServletRequest(portletRequest), portletName, key,
			defaultValue, clearCache);
	}

}