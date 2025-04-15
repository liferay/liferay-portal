/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.ServletContext;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public class ServletContextPool {

	public static void clear() {
		_servletContexts.clear();
	}

	public static boolean containsKey(String servletContextName) {
		if (servletContextName == null) {
			return false;
		}

		boolean value = _servletContexts.containsKey(servletContextName);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Contains key ", servletContextName, " ", value));
		}

		return value;
	}

	public static ServletContext get(String servletContextName) {
		ServletContext servletContext = _servletContexts.get(
			servletContextName);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Get ", servletContextName, " ", servletContext));
		}

		return servletContext;
	}

	public static Set<String> keySet() {
		return _servletContexts.keySet();
	}

	public static void put(
		String servletContextName, ServletContext servletContext) {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Put ", servletContextName, " ", servletContext));
		}

		_servletContexts.put(servletContextName, servletContext);
	}

	public static ServletContext remove(String servletContextName) {

		// We should never remove the portal context. See LPS-12683.

		String contextPath = PortalUtil.getPathContext();

		if (contextPath.equals(servletContextName)) {
			return null;
		}

		ServletContext servletContext = _servletContexts.remove(
			servletContextName);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Remove ", servletContextName, " ", servletContext));
		}

		return servletContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServletContextPool.class);

	private static final Map<String, ServletContext> _servletContexts =
		new ConcurrentHashMap<>();

}