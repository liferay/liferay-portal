/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.jsp.engine.internal.delegate;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.jasper.servlet.JspServlet;

/**
 * @author Shuyang Zhou
 */
public class CheckEnabledServletDelegate {

	public CheckEnabledServletDelegate(
		JspServlet jspServlet, ServletContext servletContext,
		long checkInterval) {

		_jspServlet = jspServlet;
		_servletContext = servletContext;
		_checkInterval = checkInterval;
	}

	public void destroy() {
		_scheduledExecutorService.shutdownNow();
	}

	public void init(ServletConfig servletConfig) throws ServletException {
		_jspServlet.init(servletConfig);

		_scheduledExecutorService = new ScheduledThreadPoolExecutor(
			1,
			runnable -> {
				Thread thread = new Thread(
					runnable,
					"Portal Jasper Servlet Background Compiler Thread");

				thread.setContextClassLoader(_servletContext.getClassLoader());
				thread.setDaemon(true);

				return thread;
			});

		_scheduledExecutorService.scheduleWithFixedDelay(
			_jspServlet::periodicEvent, _checkInterval, _checkInterval,
			TimeUnit.SECONDS);
	}

	private final long _checkInterval;
	private final JspServlet _jspServlet;
	private ScheduledExecutorService _scheduledExecutorService;
	private final ServletContext _servletContext;

}