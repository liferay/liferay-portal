/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.http.HttpSessionListener;

/**
 * <p>
 * See https://issues.liferay.com/browse/LEP-2299.
 * </p>
 *
 * @author Olaf Fricke
 * @author Brian Wing Shun Chan
 */
public class PortletSessionListenerLoader implements ServletContextListener {

	public PortletSessionListenerLoader(
		HttpSessionListener httpSessionListener) {

		_httpSessionListener = httpSessionListener;
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		PortletSessionListenerManager.removeHttpSessionListener(
			_httpSessionListener);

		_httpSessionListener = null;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		PortletSessionListenerManager.addHttpSessionListener(
			_httpSessionListener);
	}

	private HttpSessionListener _httpSessionListener;

}