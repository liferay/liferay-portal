/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * @author Neil Griffin
 */
public class SpringServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();

		if (_springBeanPortletExtension != null) {
			_springBeanPortletExtension.step5ApplicationScopeBeforeDestroyed(
				servletContext);
		}

		servletContext.removeAttribute(
			SpringBeanPortletExtension.class.getName());
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();

		_springBeanPortletExtension =
			(SpringBeanPortletExtension)servletContext.getAttribute(
				SpringBeanPortletExtension.class.getName());

		if (_springBeanPortletExtension == null) {
			_log.error("Spring's context loader listener did not initialize");

			return;
		}

		_springBeanPortletExtension.step3ApplicationScopeInitialized(
			servletContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SpringServletContextListener.class);

	private SpringBeanPortletExtension _springBeanPortletExtension;

}