/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.PortalWebResourcesUtil;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Shuyang Zhou
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link com.liferay.portal.kernel.servlet.FileAvailabilityUtil}
 */
@Deprecated
public class FileAvailabilityUtil {

	public static void clearAvailabilities() {
		ServletContext servletContext = ServletContextPool.get(
			PortalUtil.getServletContextName());

		Map<String, Boolean> availabilities =
			(Map<String, Boolean>)servletContext.getAttribute(
				FileAvailabilityUtil.class.getName());

		if (availabilities != null) {
			servletContext.removeAttribute(
				FileAvailabilityUtil.class.getName());
		}
	}

	public static boolean isAvailable(
		ServletContext servletContext, String path) {

		if (Validator.isNull(path)) {
			return false;
		}

		if (path.charAt(0) != CharPool.SLASH) {
			return true;
		}

		Map<String, Boolean> availabilities = _getAvailabilities(
			servletContext);

		Boolean available = availabilities.get(path);

		if (available != null) {
			return available;
		}

		URL url = null;

		try {
			url = servletContext.getResource(path);
		}
		catch (MalformedURLException malformedURLException) {
			if (_log.isDebugEnabled()) {
				_log.debug(malformedURLException);
			}
		}

		if ((url == null) && !PortalWebResourcesUtil.isAvailable(path)) {
			available = Boolean.FALSE;
		}
		else {
			available = Boolean.TRUE;
		}

		availabilities.put(path, available);

		return available;
	}

	private static Map<String, Boolean> _getAvailabilities(
		ServletContext servletContext) {

		Map<String, Boolean> availabilities =
			(Map<String, Boolean>)servletContext.getAttribute(
				FileAvailabilityUtil.class.getName());

		if (availabilities == null) {
			availabilities = new ConcurrentHashMap<>();

			servletContext.setAttribute(
				FileAvailabilityUtil.class.getName(), availabilities);
		}

		return availabilities;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileAvailabilityUtil.class);

}