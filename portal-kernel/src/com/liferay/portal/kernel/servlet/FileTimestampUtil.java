/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Shuyang Zhou
 */
public class FileTimestampUtil {

	public static long getTimestamp(
		ServletContext servletContext, String path) {

		if (Validator.isNull(path) || (path.charAt(0) != CharPool.SLASH)) {
			return 0;
		}

		String timestampsCacheKey = FileTimestampUtil.class.getName();

		Map<String, Long> timestamps =
			(Map<String, Long>)servletContext.getAttribute(timestampsCacheKey);

		if (timestamps == null) {
			timestamps = new ConcurrentHashMap<>();

			servletContext.setAttribute(timestampsCacheKey, timestamps);
		}

		Long timestamp = timestamps.get(path);

		if (timestamp != null) {
			return timestamp;
		}

		timestamp = 0L;

		String uriRealPath = servletContext.getRealPath(path);

		if (uriRealPath != null) {
			File uriFile = new File(uriRealPath);

			if (uriFile.exists()) {
				timestamp = uriFile.lastModified();

				timestamps.put(path, timestamp);

				return timestamp;
			}
		}

		try {
			URL url = servletContext.getResource(path);

			if (url == null) {
				_log.error("Resource URL for " + path + " is null");
			}
			else {
				timestamp = URLUtil.getLastModifiedTime(url);
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}

		timestamps.put(path, timestamp);

		return timestamp;
	}

	public static void reset(ServletContext servletContext) {
		String timestampsCacheKey = FileTimestampUtil.class.getName();

		servletContext.removeAttribute(timestampsCacheKey);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileTimestampUtil.class);

}